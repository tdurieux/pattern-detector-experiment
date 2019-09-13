/*
 * SonarQube XML Plugin
 * Copyright (C) 2010 SonarSource
 * sonarqube@googlegroups.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.sonar.plugins.xml.highlighting;

import org.junit.Test;

import javax.xml.stream.XMLStreamException;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class XMLHighlightingTest {

  @Test
  public void testCDATAWithTagsInside() throws Exception {
    List<HighlightingData> highlightingData = XMLHighlighting.getHighlightingData("<tag><![CDATA[<tag/><!-- Comment -->]]></tag>");
    assertEquals(5, highlightingData.size());
    assertData(highlightingData.get(0), 0, 4, "k");
    assertData(highlightingData.get(1), 4, 5, "k");

    assertData(highlightingData.get(2), 5, 14, "k");
    assertData(highlightingData.get(3), 36, 39, "k");

    assertData(highlightingData.get(4), 39, 45, "k");
  }

  @Test
  public void testHighlightAutoclosingTagWithAttribute() throws XMLStreamException {
    List<HighlightingData> highlightingData = XMLHighlighting.getHighlightingData("<input type='checkbox' />");
    assertEquals(4, highlightingData.size());
    assertData(highlightingData.get(2), 0, 6, "k");
    assertData(highlightingData.get(0), 7, 11, "c");
    assertData(highlightingData.get(1), 12, 22, "s");
    assertData(highlightingData.get(3), 23, 25, "k");
  }

  @Test
  public void testHighlightTagWithDoubleQuoteAttribute() throws XMLStreamException {
    List<HighlightingData> highlightingData = XMLHighlighting.getHighlightingData("<tag att=\"value ' with simple quote\"> </tag>");
    assertEquals(5, highlightingData.size());
    assertData(highlightingData.get(2), 0, 4, "k");
    assertData(highlightingData.get(0), 5, 8, "c");
    assertData(highlightingData.get(1), 9, 36, "s");
    assertData(highlightingData.get(3), 36, 37, "k");
  }

  @Test
  public void testHighlightMultilineTagWithAttributes() throws XMLStreamException {
    List<HighlightingData> highlightingData = XMLHighlighting.getHighlightingData("<tag att1='value1' \n att2\n = 'value2' att3=\n'value3' att4='multiline \n \" attribute'> </tag>");
    assertEquals(11, highlightingData.size());
    assertData(highlightingData.get(0), 5, 9, "c");
    assertData(highlightingData.get(1), 10, 18, "s");
    assertData(highlightingData.get(2), 21, 27, "c");
    assertData(highlightingData.get(3), 29, 37, "s");
    assertData(highlightingData.get(4), 38, 42, "c");
    assertData(highlightingData.get(5), 44, 52, "s");
    assertData(highlightingData.get(6), 53, 57, "c");
    assertData(highlightingData.get(7), 58, 83, "s");

    assertData(highlightingData.get(8), 0, 4, "k");
    assertData(highlightingData.get(9), 83, 84, "k");
  }

  @Test
  public void testHighlightMultilineComments() throws XMLStreamException {
    List<HighlightingData> highlightingData = XMLHighlighting.getHighlightingData("<tag><!-- hello \n" +
      " world!! --></tag>");
    assertEquals(4, highlightingData.size());
    assertData(highlightingData.get(2), 5, 29, "j");
  }

  @Test
  public void testHighlightMultilineComments_() throws XMLStreamException {
    List<HighlightingData> highlightingData = XMLHighlighting.getHighlightingData("<?xml version=\"1.0\"?>\n" +
      "  \n" +
      "<!--\n" +
      " A OnJava Journal Catalog --><tag/>");
    assertEquals(7, highlightingData.size());
    assertData(highlightingData.get(4), 25, 59, "j");
  }

  @Test
  public void testAttributeValueWithEqual() throws Exception {
    List<HighlightingData> highlightingData = XMLHighlighting.getHighlightingData("<meta content=\"charset=UTF-8\" />");
    assertEquals(4, highlightingData.size());
    assertData(highlightingData.get(1), 14, 29, "s");
  }

  @Test
  public void testHighlightCommentsAndOtherTag() throws XMLStreamException {
    List<HighlightingData> highlightingData = XMLHighlighting.getHighlightingData("<!-- comment --><tag/>");
    assertEquals(3, highlightingData.size());
    assertData(highlightingData.get(0), 0, 16, "j");
  }

  @Test
  public void testHighlightDoctype() throws XMLStreamException {
    List<HighlightingData> highlightingData = XMLHighlighting.getHighlightingData("<!DOCTYPE foo> <tag/>");
    assertEquals(4, highlightingData.size());
    assertData(highlightingData.get(0), 0, 9, "j");
    assertData(highlightingData.get(1), 13, 14, "j");
  }

  @Test
  public void testCDATA() throws XMLStreamException {
    List<HighlightingData> highlightingData = XMLHighlighting.getHighlightingData("<tag><![CDATA[foo]]></tag>");
    assertEquals(5, highlightingData.size());
    assertData(highlightingData.get(2), 5, 14, "k");
    assertData(highlightingData.get(3), 17, 20, "k");
  }

  @Test
  public void testCDATAMultiline() throws XMLStreamException {
    List<HighlightingData> highlightingData = XMLHighlighting.getHighlightingData(
      "<tag><![CDATA[foo\n" +
        "bar\n" +
        "]]></tag>");
    assertEquals(5, highlightingData.size());
    assertData(highlightingData.get(2), 5, 14, "k");
    assertData(highlightingData.get(3), 22, 25, "k");
  }

  @Test
  public void testHighlightTag() throws XMLStreamException {
    List<HighlightingData> highlightingData = XMLHighlighting.getHighlightingData("<tr></tr>");
    assertEquals(3, highlightingData.size());
    assertData(highlightingData.get(0), 0, 3, "k");
    assertData(highlightingData.get(1), 3, 4, "k");
    assertData(highlightingData.get(2), 4, 9, "k");
  }

  @Test
  public void testEmptyElement() throws Exception {
    List<HighlightingData> highlightingData = XMLHighlighting.getHighlightingData("<br/>");
    assertEquals(2, highlightingData.size());
    assertData(highlightingData.get(0), 0, 3, "k");
    assertData(highlightingData.get(1), 3, 5, "k");
  }

  @Test
  public void testSpacesInside() throws Exception {
    List<HighlightingData> highlightingData = XMLHighlighting.getHighlightingData("<tag > </tag >");
    assertEquals(3, highlightingData.size());
    assertData(highlightingData.get(0), 0, 4, "k");
    assertData(highlightingData.get(1), 5, 6, "k");
    assertData(highlightingData.get(2), 7, 14, "k");

    highlightingData = XMLHighlighting.getHighlightingData("<tag />");
    assertEquals(2, highlightingData.size());
    assertData(highlightingData.get(0), 0, 4, "k");
    assertData(highlightingData.get(1), 5, 7, "k");
  }

  @Test
  public void testHighlightTagWithNamespace() throws XMLStreamException {
    List<HighlightingData> highlightingData = XMLHighlighting.getHighlightingData("<tag xmlns:x='url'>\n<x:table>  </x:table></tag>");
    assertEquals(8, highlightingData.size());
    assertData(highlightingData.get(0), 5, 12, "c");
    assertData(highlightingData.get(1), 13, 18, "s");
    assertData(highlightingData.get(4), 20, 28, "k");
    assertData(highlightingData.get(5), 28, 29, "k");
  }

  @Test
  public void testXMLHeader() throws XMLStreamException {
    List<HighlightingData> highlightingData = XMLHighlighting.getHighlightingData("<?xml version=\"1.0\" encoding=\"UTF-8\" ?> <tag/>");
    assertEquals(8, highlightingData.size());
    assertData(highlightingData.get(0), 0, 5, "k");
    assertData(highlightingData.get(1), 37, 39, "k");

    assertData(highlightingData.get(2), 6, 13, "c");
    assertData(highlightingData.get(3), 14, 19, "s");
    assertData(highlightingData.get(4), 20, 28, "c");
    assertData(highlightingData.get(5), 29, 36, "s");
  }

  private void assertData(HighlightingData data, Integer start, Integer end, String code) {
    assertEquals(start, data.startOffset());
    assertEquals(end, data.endOffset());
    assertEquals(code, data.highlightCode());
  }

}
