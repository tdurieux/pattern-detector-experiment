  + native
package org.apache.lucene.search.postingshighlight;

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

import java.text.BreakIterator;
import java.text.CharacterIterator;

/** Just produces one single fragment for the entire
 *  string. */
final class WholeBreakIterator extends BreakIterator {
  private CharacterIterator text;
  private int len;
  private int current;

  @Override
  public int current() {
    return current;
  }

  @Override
  public int first() {
    return (current = 0);
  }

  @Override
  public int following(int pos) {
    if (pos < 0 || pos > len) {
      throw new IllegalArgumentException("offset out of bounds");
    } else if (pos == len) {
      return DONE;
    } else {
      return last();
    }
  }

  @Override
  public CharacterIterator getText() {
    return text;
  }

  @Override
  public int last() {
    return (current = len);
  }

  @Override
  public int next() {
    if (current == len) {
      return DONE;
    } else {
      return last();
    }
  }

  @Override
  public int next(int n) {
    if (n < 0) {
      for (int i = 0; i < -n; i++) {
        previous();
      }
    } else {
      for (int i = 0; i < n; i++) {
        next();
      }
    }
    return current();
  }

  @Override
  public int preceding(int pos) {
    if (pos < 0 || pos > len) {
      throw new IllegalArgumentException("offset out of bounds");
    } else if (pos == 0) {
      return DONE;
    } else {
      return first();
    }
  }

  @Override
  public int previous() {
    if (current == 0) {
      return DONE;
    } else {
      return first();
    }
  }

  @Override
  public void setText(CharacterIterator newText) {
    if (newText.getBeginIndex() != 0) {
      throw new UnsupportedOperationException();
    }
    len = newText.getEndIndex();
    text = newText;
    current = 0;
  }
}
