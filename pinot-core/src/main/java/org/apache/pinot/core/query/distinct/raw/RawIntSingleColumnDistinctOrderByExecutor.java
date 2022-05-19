/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.pinot.core.query.distinct.raw;

import it.unimi.dsi.fastutil.PriorityQueue;
import it.unimi.dsi.fastutil.objects.ObjectHeapPriorityQueue;
import org.apache.pinot.common.request.context.ExpressionContext;
import org.apache.pinot.common.request.context.OrderByExpressionContext;
import org.apache.pinot.core.common.BlockValSet;
import org.apache.pinot.core.operator.blocks.TransformBlock;
import org.apache.pinot.core.query.distinct.DistinctExecutor;
import org.apache.pinot.spi.data.FieldSpec;
import org.roaringbitmap.buffer.ImmutableRoaringBitmap;


/**
 * {@link DistinctExecutor} for distinct order-by queries with single raw INT column.
 */
public class RawIntSingleColumnDistinctOrderByExecutor extends BaseRawIntSingleColumnDistinctExecutor {
  private final PriorityQueue<Integer> _priorityQueue;

  public RawIntSingleColumnDistinctOrderByExecutor(ExpressionContext expression, FieldSpec fieldSpec,
      OrderByExpressionContext orderByExpression, int limit) {
    super(expression, fieldSpec, limit);

    assert orderByExpression.getExpression().equals(expression);
    int comparisonFactor = orderByExpression.isAsc() ? -1 : 1;
    _priorityQueue = new ObjectHeapPriorityQueue<Integer>(Math.min(limit, MAX_INITIAL_CAPACITY),
        (i1, i2) -> i1 == null ? (i2 == null ? 0 : 1) : (i2 == null ? -1 : Integer.compare(i1, i2)) * comparisonFactor);
  }

  @Override
  public boolean process(TransformBlock transformBlock) {
    BlockValSet blockValueSet = transformBlock.getBlockValueSet(_expression);
    int[] values = blockValueSet.getIntValuesSV();
    ImmutableRoaringBitmap nullBitmap = blockValueSet.getNullBitmap();
    int numDocs = transformBlock.getNumDocs();
    for (int i = 0; i < numDocs; i++) {
      Integer value = nullBitmap.contains(i) ? null : values[i];
      if (!_valueSet.contains(value)) {
        if (_valueSet.size() < _limit) {
          _valueSet.add(value);
          _priorityQueue.enqueue(value);
        } else {
          Integer firstValue = _priorityQueue.first();
          if (_priorityQueue.comparator().compare(value, firstValue) > 0) {
            _valueSet.remove(firstValue);
            _valueSet.add(value);
            _priorityQueue.dequeue();
            _priorityQueue.enqueue(value);
          }
        }
      }
    }
    return false;
  }
}
