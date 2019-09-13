/*
 * SonarQube PHP Plugin
 * Copyright (C) 2010-2017 SonarSource SA
 * mailto:info AT sonarsource DOT com
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package org.sonar.plugins.php.phpunit;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.sonar.api.batch.sensor.SensorContext;
import org.sonar.api.utils.Version;
import org.sonar.plugins.php.PhpPlugin;

public class CompatibilityImportersFactory {

  public static final String DEPRECATION_WARNING_TEMPLATE = "%s is deprecated as of SonarQube 6.2. It still works, but please consider using "
    + PhpPlugin.PHPUNIT_COVERAGE_REPORT_PATHS_KEY + ".";
  public static final String SKIPPED_WARNING_TEMPLATE = "Ignoring %s since you are already using " + PhpPlugin.PHPUNIT_COVERAGE_REPORT_PATHS_KEY + ". Please remove %<s.";
  private final SensorContext context;

  public CompatibilityImportersFactory(SensorContext context) {
    this.context = context;
  }

  public List<PhpUnitImporter> createImporters() {
    final ArrayList<PhpUnitImporter> importers = new ArrayList<>();
    importers.add(new PhpUnitTestResultImporter());
    if (supportsMultiPathCoverage() && multiPathCoverageUsed()) {
      addMultiCoverageImporter(importers);
    } else {
      addLegacyImporters(importers);
    }
    return importers;
  }

  private boolean supportsMultiPathCoverage() {
    return context.getSonarQubeVersion().isGreaterThanOrEqual(Version.create(6, 2));
  }

  private boolean multiPathCoverageUsed() {
    return context.settings().getStringArray(PhpPlugin.PHPUNIT_COVERAGE_REPORT_PATHS_KEY).length > 0;
  }

  private void addMultiCoverageImporter(ArrayList<PhpUnitImporter> importers) {
    importers.add(new MultiPathImporter(new PhpUnitCoverageResultImporter(), PhpPlugin.PHPUNIT_COVERAGE_REPORT_PATHS_KEY, "coverage"));
  }

  private void addLegacyImporters(ArrayList<PhpUnitImporter> importers) {
    importers.add(new PhpUnitCoverageResultImporter());
    importers.add(new PhpUnitItCoverageResultImporter());
    importers.add(new PhpUnitOverallCoverageResultImporter());
  }

  public List<String> deprecationWarnings() {
    if (supportsMultiPathCoverage()) {
      if (multiPathCoverageUsed()) {
        return usedLegacyCoveragePaths().map(pathKey -> String.format(SKIPPED_WARNING_TEMPLATE, pathKey)).collect(Collectors.toList());
      } else {
        return usedLegacyCoveragePaths().map(pathKey -> String.format(DEPRECATION_WARNING_TEMPLATE, pathKey)).collect(Collectors.toList());
      }
    }
    return new ArrayList<>();
  }

  private Stream<String> usedLegacyCoveragePaths() {
    final Stream.Builder<String> streamBuilder = Stream.builder();
    addToStreamIfUsed(streamBuilder, PhpPlugin.PHPUNIT_COVERAGE_REPORT_PATH_KEY);
    addToStreamIfUsed(streamBuilder, PhpPlugin.PHPUNIT_IT_COVERAGE_REPORT_PATH_KEY);
    addToStreamIfUsed(streamBuilder, PhpPlugin.PHPUNIT_OVERALL_COVERAGE_REPORT_PATH_KEY);
    return streamBuilder.build();
  }

  private void addToStreamIfUsed(Stream.Builder<String> streamBuilder, String legacyPathKey) {
    if (context.settings().getString(legacyPathKey) != null) {
      streamBuilder.add(legacyPathKey);
    }
  }
}
