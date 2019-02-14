  Merged /lucene/dev/branches/lucene4547/lucene/queries/src/test/org/apache/lucene/queries/function/TestFunctionQuerySort.java:r1407149-1443597
  Merged /lucene/dev/branches/cleanup2878/lucene/queries/src/test/org/apache/lucene/queries/function/TestFunctionQuerySort.java:r1403701-1403781
  Merged /lucene/dev/branches/lucene3846/lucene/queries/src/test/org/apache/lucene/queries/function/TestFunctionQuerySort.java:r1397170-1403761
  Merged /lucene/dev/branches/lucene4765/lucene/queries/src/test/org/apache/lucene/queries/function/TestFunctionQuerySort.java:r1444646-1447981
  Merged /lucene/dev/branches/lucene4199/lucene/queries/src/test/org/apache/lucene/queries/function/TestFunctionQuerySort.java:r1358548-1359191
  Merged /lucene/dev/branches/lucene3312/lucene/queries/src/test/org/apache/lucene/queries/function/TestFunctionQuerySort.java:r1357905-1379945
  Merged /lucene/dev/branches/lucene3969/lucene/queries/src/test/org/apache/lucene/queries/function/TestFunctionQuerySort.java:r1311219-1324948
  Merged /lucene/dev/branches/branch_3x/lucene/queries/src/test/org/apache/lucene/queries/function/TestFunctionQuerySort.java:r1232954,1302749,1302808,1303007,1303023,1303269,1303733,1303854,1304295,1304360,1304660,1304904,1305074,1305142,1305681,1305693,1305719,1305741,1305816,1305837,1306929,1307050
  Merged /lucene/dev/branches/branch_4x/lucene/queries/src/test/org/apache/lucene/queries/function/TestFunctionQuerySort.java:r1344391,1344929,1348012,1348274,1348293,1348919,1348951,1349048,1349340,1349446,1349991,1353701,1355203,1356608,1359358,1363876,1364063,1364069,1449183,1467413
  Merged /lucene/dev/branches/lucene4055/lucene/queries/src/test/org/apache/lucene/queries/function/TestFunctionQuerySort.java:r1338960-1343359
  Merged /lucene/dev/branches/pforcodec_3892/lucene/queries/src/test/org/apache/lucene/queries/function/TestFunctionQuerySort.java:r1352188-1375470
  + native
package org.apache.lucene.queries.function;

/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import org.apache.lucene.index.AtomicReaderContext;
import org.apache.lucene.search.FieldComparator;
import org.apache.lucene.search.FieldComparatorSource;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.SortField;

import java.io.IOException;
import java.util.IdentityHashMap;
import java.util.Map;

/**
 * Instantiates {@link FunctionValues} for a particular reader.
 * <br>
 * Often used when creating a {@link FunctionQuery}.
 *
 *
 */
public abstract class ValueSource {

  /**
   * Gets the values for this reader and the context that was previously
   * passed to createWeight()
   */
  public abstract FunctionValues getValues(Map context, AtomicReaderContext readerContext) throws IOException;

  @Override
  public abstract boolean equals(Object o);

  @Override
  public abstract int hashCode();

  /**
   * description of field, used in explain()
   */
  public abstract String description();

  @Override
  public String toString() {
    return description();
  }


  /**
   * Implementations should propagate createWeight to sub-ValueSources which can optionally store
   * weight info in the context. The context object will be passed to getValues()
   * where this info can be retrieved.
   */
  public void createWeight(Map context, IndexSearcher searcher) throws IOException {
  }

  /**
   * Returns a new non-threadsafe context map.
   */
  public static Map newContext(IndexSearcher searcher) {
    Map context = new IdentityHashMap();
    context.put("searcher", searcher);
    return context;
  }


  //
  // Sorting by function
  //

  /**
   * EXPERIMENTAL: This method is subject to change.
   * <p>
   * Get the SortField for this ValueSource.  Uses the {@link #getValues(java.util.Map, AtomicReaderContext)}
   * to populate the SortField.
   *
   * @param reverse true if this is a reverse sort.
   * @return The {@link org.apache.lucene.search.SortField} for the ValueSource
   */
  public SortField getSortField(boolean reverse) {
    return new ValueSourceSortField(reverse);
  }

  class ValueSourceSortField extends SortField {
    public ValueSourceSortField(boolean reverse) {
      super(description(), SortField.Type.REWRITEABLE, reverse);
    }

    @Override
    public SortField rewrite(IndexSearcher searcher) throws IOException {
      Map context = newContext(searcher);
      createWeight(context, searcher);
      return new SortField(getField(), new ValueSourceComparatorSource(context), getReverse());
    }
  }

  class ValueSourceComparatorSource extends FieldComparatorSource {
    private final Map context;

    public ValueSourceComparatorSource(Map context) {
      this.context = context;
    }

    @Override
    public FieldComparator<Double> newComparator(String fieldname, int numHits,
                                         int sortPos, boolean reversed) throws IOException {
      return new ValueSourceComparator(context, numHits);
    }
  }

  /**
   * Implement a {@link org.apache.lucene.search.FieldComparator} that works
   * off of the {@link FunctionValues} for a ValueSource
   * instead of the normal Lucene FieldComparator that works off of a FieldCache.
   */
  class ValueSourceComparator extends FieldComparator<Double> {
    private final double[] values;
    private FunctionValues docVals;
    private double bottom;
    private final Map fcontext;

    ValueSourceComparator(Map fcontext, int numHits) {
      this.fcontext = fcontext;
      values = new double[numHits];
    }

    @Override
    public int compare(int slot1, int slot2) {
      final double v1 = values[slot1];
      final double v2 = values[slot2];
      if (v1 > v2) {
        return 1;
      } else if (v1 < v2) {
        return -1;
      } else {
        return 0;
      }

    }

    @Override
    public int compareBottom(int doc) {
      final double v2 = docVals.doubleVal(doc);
      if (bottom > v2) {
        return 1;
      } else if (bottom < v2) {
        return -1;
      } else {
        return 0;
      }
    }

    @Override
    public void copy(int slot, int doc) {
      values[slot] = docVals.doubleVal(doc);
    }

    @Override
    public FieldComparator setNextReader(AtomicReaderContext context) throws IOException {
      docVals = getValues(fcontext, context);
      return this;
    }

    @Override
    public void setBottom(final int bottom) {
      this.bottom = values[bottom];
    }

    @Override
    public Double value(int slot) {
      return values[slot];
    }

    @Override
    public int compareDocToValue(int doc, Double valueObj) {
      final double value = valueObj;
      final double docValue = docVals.doubleVal(doc);
      if (docValue < value) {
        return -1;
      } else if (docValue > value) {
        return -1;
      } else {
        return 0;
      }
    }
  }
}
