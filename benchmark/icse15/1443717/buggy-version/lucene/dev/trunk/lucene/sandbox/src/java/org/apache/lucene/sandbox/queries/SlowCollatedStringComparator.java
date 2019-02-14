  Merged /lucene/dev/branches/lucene4547/lucene/misc:r1407149-1443597
  Merged /lucene/dev/branches/lucene4055/lucene/classification:r1338960-1343359
  Merged /lucene/dev/branches/pforcodec_3892/lucene/classification:r1352188-1375470
  Merged /lucene/dev/branches/ghost_of_4456/lucene/classification:r1394211-1394305
  Merged /lucene/dev/branches/lucene4446/lucene/classification:r1397400-1398082
  Merged /lucene/dev/branches/lucene4547/lucene/classification:r1407149-1443597
  Merged /lucene/dev/branches/solr3733/lucene/classification:r1388080-1388269
  Merged /lucene/dev/branches/lucene3846/lucene/classification:r1397170-1403761
  Merged /lucene/dev/branches/lucene4199/lucene/classification:r1358548-1359191
  Merged /lucene/dev/branches/slowclosing/lucene/classification:r1393532-1393785
  Merged /lucene/dev/branches/lucene3969/lucene/classification:r1311219-1324948
  Merged /lucene/dev/branches/branch_3x/lucene/classification:r1232954,1302749,1302808,1303007,1303023,1303269,1303733,1303854,1304295,1304360,1304660,1304904,1305074,1305142,1305681,1305693,1305719,1305741,1305816,1305837,1306929,1307050
  Merged /lucene/dev/branches/branch_4x/lucene/classification:r1344391,1344929,1348012,1348274,1348293,1348919,1348951,1349048,1349340,1349446,1349991,1353701,1355203,1356608,1359358,1363876,1364063,1364069,1367391,1367489,1367833,1368975,1369226,1371960,1374622,1375497,1375558,1376547,1378442,1378591,1379175,1380802,1381204,1383216,1386921,1388425,1389811,1389929,1392460,1393832,1394309,1395515,1404227,1405891
  Merged /lucene/dev/branches/lucene_solr_4_0/lucene/classification:r1388937,1389448,1390046,1394306
  Merged /lucene/dev/branches/cleanup2878/lucene/classification:r1403701-1403781
  Merged /lucene/dev/branches/lucene2510/lucene/classification:r1364862-1365496
  Merged /lucene/dev/branches/lucene3312/lucene/classification:r1357905-1379945
package org.apache.lucene.sandbox.queries;

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

import java.io.IOException;
import java.text.Collator;

import org.apache.lucene.index.AtomicReaderContext;
import org.apache.lucene.search.FieldCache;
import org.apache.lucene.search.FieldCache.DocTerms;
import org.apache.lucene.search.FieldComparator;
import org.apache.lucene.util.BytesRef;

/** Sorts by a field's value using the given Collator
 *
 * <p><b>WARNING</b>: this is very slow; you'll
 * get much better performance using the
 * CollationKeyAnalyzer or ICUCollationKeyAnalyzer. 
 * @deprecated Index collation keys with CollationKeyAnalyzer or ICUCollationKeyAnalyzer instead.
 * This class will be removed in Lucene 5.0
 */
@Deprecated
public final class SlowCollatedStringComparator extends FieldComparator<String> {

  private final String[] values;
  private DocTerms currentDocTerms;
  private final String field;
  final Collator collator;
  private String bottom;
  private final BytesRef tempBR = new BytesRef();

  public SlowCollatedStringComparator(int numHits, String field, Collator collator) {
    values = new String[numHits];
    this.field = field;
    this.collator = collator;
  }

  @Override
  public int compare(int slot1, int slot2) {
    final String val1 = values[slot1];
    final String val2 = values[slot2];
    if (val1 == null) {
      if (val2 == null) {
        return 0;
      }
      return -1;
    } else if (val2 == null) {
      return 1;
    }
    return collator.compare(val1, val2);
  }

  @Override
  public int compareBottom(int doc) {
    final String val2 = currentDocTerms.getTerm(doc, tempBR).utf8ToString();
    if (bottom == null) {
      if (val2 == null) {
        return 0;
      }
      return -1;
    } else if (val2 == null) {
      return 1;
    }
    return collator.compare(bottom, val2);
  }

  @Override
  public void copy(int slot, int doc) {
    final BytesRef br = currentDocTerms.getTerm(doc, tempBR);
    if (br == null) {
      values[slot] = null;
    } else {
      values[slot] = br.utf8ToString();
    }
  }

  @Override
  public FieldComparator<String> setNextReader(AtomicReaderContext context) throws IOException {
    currentDocTerms = FieldCache.DEFAULT.getTerms(context.reader(), field);
    return this;
  }
  
  @Override
  public void setBottom(final int bottom) {
    this.bottom = values[bottom];
  }

  @Override
  public String value(int slot) {
    return values[slot];
  }

  @Override
  public int compareValues(String first, String second) {
    if (first == null) {
      if (second == null) {
        return 0;
      }
      return -1;
    } else if (second == null) {
      return 1;
    } else {
      return collator.compare(first, second);
    }
  }

  @Override
  public int compareDocToValue(int doc, String value) {
    final BytesRef br = currentDocTerms.getTerm(doc, tempBR);
    final String docValue;
    if (br == null) {
      docValue = null;
    } else {
      docValue = br.utf8ToString();
    }
    return compareValues(docValue, value);
  }
}
