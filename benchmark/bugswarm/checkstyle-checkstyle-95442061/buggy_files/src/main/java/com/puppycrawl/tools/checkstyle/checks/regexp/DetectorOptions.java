////////////////////////////////////////////////////////////////////////////////
// checkstyle: Checks Java source code for adherence to a set of rules.
// Copyright (C) 2001-2015 the original author or authors.
//
// This library is free software; you can redistribute it and/or
// modify it under the terms of the GNU Lesser General Public
// License as published by the Free Software Foundation; either
// version 2.1 of the License, or (at your option) any later version.
//
// This library is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
// Lesser General Public License for more details.
//
// You should have received a copy of the GNU Lesser General Public
// License along with this library; if not, write to the Free Software
// Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
////////////////////////////////////////////////////////////////////////////////

package com.puppycrawl.tools.checkstyle.checks.regexp;

import java.util.regex.Pattern;

import org.apache.commons.lang3.ObjectUtils;

import com.puppycrawl.tools.checkstyle.api.AbstractViolationReporter;

/**
 * Options for a detector.
 * @author Oliver Burn
 */
final class DetectorOptions {

    /** Flags to compile a regular expression with. See {@link Pattern#flags()}. */
    private int compileFlags;
    /** Used for reporting violations. */
    private AbstractViolationReporter reporter;
    /**
     * Format of the regular expression to check for.
     */
    private String format;
    /** The message to report on detection. If blank, then use the format. */
    private String message;
    /** Minimum number of times regular expression should occur in a file. */
    private int minimum;
    /** Maximum number of times regular expression should occur in a file. */
    private int maximum;
    /** Whether to ignore case when matching. */
    private boolean ignoreCase;
    /** Used to determine whether to suppress a detected match. */
    private MatchSuppressor suppressor;
    /** Pattern created from format. Lazily initialized. */
    private Pattern pattern;

    /** Default constructor. */
    private DetectorOptions() { }

    /**
     * Format of the regular expression.
     * @return format of the regular expression.
     */
    public String getFormat() {
        return format;
    }

    /**
     * The violation reporter to use.
     * @return the violation reporter to use.
     */
    public AbstractViolationReporter getReporter() {
        return reporter;
    }

    /**
     * The message to report errors with.
     * @return the message to report errors with.
     */
    public String getMessage() {
        return message;
    }

    /**
     * The minimum number of allowed detections.
     * @return the minimum number of allowed detections.
     */
    public int getMinimum() {
        return minimum;
    }

    /**
     * The maximum number of allowed detections.
     * @return the maximum number of allowed detections.
     */
    public int getMaximum() {
        return maximum;
    }

    /**
     * The suppressor to use.
     * @return the suppressor to use.
     */
    public MatchSuppressor getSuppressor() {
        return suppressor;
    }

    /**
     * The pattern to use when matching.
     * @return the pattern to use when matching.
     */
    public Pattern getPattern() {
        if (pattern != null) {
            return pattern;
        }
        int options = compileFlags;

        if (ignoreCase) {
            options |= Pattern.CASE_INSENSITIVE;
        }
        pattern = Pattern.compile(format, options);
        return pattern;
    }

    /**
     * Creates new Builder instance.
     * @return Builder instance.
     */
    public static Builder newBuilder() {
        return new DetectorOptions().new Builder();
    }

    /** Class which implements Builder pattern to build DetectorOptions instance. */
    final class Builder {

        /** Default constructor. */
        private Builder() { }

        /**
         * Specifies the format to use when matching lines and returns Builder object.
         * @param val the format to use when matching lines.
         * @return Builder object.
         */
        public Builder format(String val) {
            format = val;
            return this;
        }

        /**
         * Specifies message to use when reporting a match and returns Builder object.
         * @param val message to use when reporting a match.
         * @return Builder object.
         */
        public Builder message(String val) {
            message = val;
            return this;
        }

        /**
         * Specifies the minimum allowed number of detections and returns Builder object.
         * @param min the minimum allowed number of detections.
         * @return Builder object.
         */
        public Builder minimum(int min) {
            minimum = min;
            return this;
        }

        /**
         * Specifies the maximum allowed number of detections and returns Builder object.
         * @param max the maximum allowed number of detections.
         * @return Builder object.
         */
        public Builder maximum(int max) {
            maximum = max;
            return this;
        }

        /**
         * Specifies whether to ignore case when matching and returns Builder object.
         * @param ignore whether to ignore case when matching.
         * @return Builder object.
         */
        public Builder ignoreCase(boolean ignore) {
            ignoreCase = ignore;
            return this;
        }

        /**
         * Specifies a reporter which is used for reporting violations and returns Builder object.
         * @param violationReporter violation reporter.
         * @return Builder object.
         */
        public Builder reporter(AbstractViolationReporter violationReporter) {
            reporter = violationReporter;
            return this;
        }

        /**
         * Specifies flags to compile a regular expression with and returns Builder object.
         * @param flags compile flags.
         * @return Builder object.
         */
        public Builder compileFlags(int flags) {
            compileFlags = flags;
            return this;
        }

        /**
         * Specifies the suppressor to use and returns Builder object.
         * @param sup the suppressor to use.
         * @return current instance
         */
        public Builder suppressor(MatchSuppressor sup) {
            suppressor = sup;
            return this;
        }

        /**
         * Returns new DetectorOptions instance.
         * @return DetectorOptions instance.
         */
        public DetectorOptions build() {
            message = ObjectUtils.defaultIfNull(message, "");
            suppressor = ObjectUtils.defaultIfNull(suppressor, NeverSuppress.INSTANCE);
            return DetectorOptions.this;
        }
    }
}
