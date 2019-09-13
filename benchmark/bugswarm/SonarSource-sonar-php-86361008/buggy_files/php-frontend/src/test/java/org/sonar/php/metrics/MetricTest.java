package org.sonar.php.metrics;

import com.google.common.base.Charsets;
import com.sonar.sslr.api.typed.ActionParser;
import org.sonar.php.parser.PHPLexicalGrammar;
import org.sonar.php.parser.PHPParserBuilder;
import org.sonar.plugins.php.api.tree.CompilationUnitTree;
import org.sonar.plugins.php.api.tree.Tree;

import java.io.File;

public class MetricTest {

  protected ActionParser<Tree> p = PHPParserBuilder.createParser(PHPLexicalGrammar.COMPILATION_UNIT, Charsets.UTF_8);

  protected CompilationUnitTree parse(String filename) {
    File file = new File("src/test/resources/metrics/", filename);

    ActionParser<Tree> parser = PHPParserBuilder.createParser(Charsets.UTF_8);
    return (CompilationUnitTree) parser.parse(file);
  }
}
