import javax.xml.stream.XMLInputFactory;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.XMLConstants;

class Stax {

  XMLInputFactory no_property() {
    XMLInputFactory factory = XMLInputFactory.newInstance(); // Noncompliant
    return factory;
  }

  XMLInputFactory unrelated_property() {
    XMLInputFactory factory = XMLInputFactory.newInstance(); // Noncompliant
    factory.setProperty(XMLInputFactory.IS_COALESCING, Boolean.FALSE);
    return factory;
  }

  XMLInputFactory external_entities_with_false_value() {
    XMLInputFactory factory = XMLInputFactory.newInstance();
    factory.setProperty(XMLInputFactory.IS_SUPPORTING_EXTERNAL_ENTITIES, Boolean.FALSE);
    return factory;
  }

  XMLInputFactory external_entities_with_true_value() {
    XMLInputFactory factory = XMLInputFactory.newInstance(); // Noncompliant
    factory.setProperty(XMLInputFactory.IS_SUPPORTING_EXTERNAL_ENTITIES, Boolean.TRUE);
    return factory;
  }

  XMLInputFactory dtd_with_false_value() {
    XMLInputFactory factory = XMLInputFactory.newInstance();
    factory.setProperty(XMLInputFactory.SUPPORT_DTD, Boolean.FALSE);
    return factory;
  }

  XMLInputFactory dtd_with_primitive_false() {
    XMLInputFactory factory = XMLInputFactory.newInstance();
    factory.setProperty(XMLInputFactory.SUPPORT_DTD, false);
    return factory;
  }

  XMLInputFactory dtd_with_primitive_true() {
    XMLInputFactory factory = XMLInputFactory.newInstance(); // Noncompliant
    factory.setProperty(XMLInputFactory.SUPPORT_DTD, true);
    return factory;
  }

  XMLInputFactory dtd_with_unknown_value(Object value) {
    XMLInputFactory factory = XMLInputFactory.newInstance(); // Noncompliant
    factory.setProperty(XMLInputFactory.SUPPORT_DTD, value);
    return factory;
  }

}

class SAXParser {

  SAXParserFactory no_property() {
    SAXParserFactory factory = SAXParserFactory.newInstance(); // Noncompliant
    return factory;
  }

  SAXParserFactory secure_processing_true() {
    SAXParserFactory factory = SAXParserFactory.newInstance();
    factory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
    return factory;
  }

  SAXParserFactory secure_processing_false() {
    SAXParserFactory factory = SAXParserFactory.newInstance(); // Noncompliant
    factory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, false);
    return factory;
  }

  SAXParserFactory other_feature_set_to_true() {
    SAXParserFactory factory = SAXParserFactory.newInstance();
    factory.setFeature("http://javax.xml.XMLConstants/feature/secure-processing", true);
    return factory;
  }

  SAXParserFactory other_feature_set_to_true() {
    SAXParserFactory factory = SAXParserFactory.newInstance(); // Noncompliant
    factory.setFeature("xxx", true);
    return factory;
  }

}
