/*
 * Copyright (c) 2017, Joyent, Inc. All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.joyent.manta.client;

import com.joyent.manta.exception.MantaClientHttpResponseException;
import com.joyent.manta.exception.MantaErrorCode;
import com.joyent.manta.http.MantaHttpHeaders;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static com.joyent.manta.client.MantaClient.SEPARATOR;
import static com.joyent.manta.util.MantaUtils.prefixPaths;
import static com.joyent.manta.util.MantaUtils.writeablePrefixPaths;

abstract class MantaRecursiveDirectoryCreationStrategy {

    private static final Logger LOG = LoggerFactory.getLogger(MantaRecursiveDirectoryCreationStrategy.class);

    final MantaClient client;
    final AtomicInteger operations;

    private MantaRecursiveDirectoryCreationStrategy(final MantaClient client) {
        this.client = client;
        operations = new AtomicInteger(0);
    }

    abstract void create(String rawPath, MantaHttpHeaders headers) throws IOException;

    /**
     * Try to create a directory and unpack the error.
     *
     * @return whether or not the directory was created successfully
     */
    Boolean createNewDirectory(final String path, final MantaHttpHeaders headers) throws IOException {
        try {
            return client.putDirectory(path, headers);
        } catch (final MantaClientHttpResponseException mchre) {
            if (mchre.getServerCode().equals(MantaErrorCode.DIRECTORY_DOES_NOT_EXIST_ERROR)) {
                return null;
            } else {
                throw mchre;
            }
        }
    }

    static class Pessimistic extends MantaRecursiveDirectoryCreationStrategy {

        Pessimistic(final MantaClient client) {
            super(client);
        }

        public void create(final String rawPath, final MantaHttpHeaders headers) throws IOException {
            final String[] parts = rawPath.split(SEPARATOR);
            final Iterator<Path> itr = Paths.get("", parts).iterator();
            final StringBuilder sb = new StringBuilder(SEPARATOR);

            for (int i = 0; itr.hasNext(); i++) {
                final String part = itr.next().toString();
                sb.append(part);

                // This means we aren't in the home nor in the reserved
                // directory path (stor, public, jobs, etc)
                if (i > 1) {
                    client.putDirectory(sb.toString(), headers);
                }

                if (itr.hasNext()) {
                    sb.append(SEPARATOR);
                }
            }
        }

    }

    static class Optimistic extends MantaRecursiveDirectoryCreationStrategy {

        Optimistic(final MantaClient client) {
            super(client);
        }

        public void create(final String rawPath, final MantaHttpHeaders headers) throws IOException {
            final List<String> ascendingDirectories = Arrays.asList(prefixPaths(rawPath));

            // reverse the list so the deepest directories are attempted first
            Collections.reverse(ascendingDirectories);
            final ArrayList<String> failedPuts = new ArrayList<>();

            for (final String intermediateDirectory : ascendingDirectories) {
                final Boolean createResult = createNewDirectory(intermediateDirectory, headers);
                if (createResult == null) {
                    failedPuts.add(intermediateDirectory);
                }
            }

            // failed puts will have the deepest directories first, reversing it will allow us to work downwards towards
            // the requested directory
            Collections.reverse(failedPuts);

            for (final String descendingDirectory : failedPuts) {
                client.putDirectory(descendingDirectory, headers);
            }
        }
    }

    static class Probe extends MantaRecursiveDirectoryCreationStrategy {

        Probe(final MantaClient client) {
            super(client);
        }

        public void create(final String rawPath, final MantaHttpHeaders headers) throws IOException {
            LOG.info("START  " + rawPath);
            final String[] paths = writeablePrefixPaths(rawPath);

            if (paths.length < 3) {
                new Pessimistic(client).create(rawPath, headers);
                return;
            }

            int min = 0;
            int max = paths.length - 1;
            int idx = Math.floorDiv(paths.length, 2);

            while (true) {
                final String currentPath = paths[idx];
                final Boolean requiredPut = createNewDirectory(currentPath, headers);

                // we successfully created the requested directory, who cares if it's redundant?
                if (requiredPut != null && rawPath.equals(currentPath)) {
                    return;
                }

                final boolean descending;
                if (requiredPut == null) {
                    LOG.info("FAILED " + currentPath);
                    descending = false;
                    max = idx;
                } else if (requiredPut) {
                    LOG.info("REQD   " + currentPath);
                    // stop skipping around and create the remaining directories normally
                    break;
                } else {
                    LOG.info("REDUN  " + currentPath);
                    descending = true;
                    min = idx;
                }

                final int jumpDistance = Math.floorDiv(max - min, 2);

                if (jumpDistance == 0) {
                    break;
                }

                if (descending) {
                    idx = idx + jumpDistance;
                } else {
                    idx = idx - jumpDistance;
                }
            }

            LOG.info("STOP SKIPPING");

            // create remaining directories normally
            for (; idx < paths.length; idx++) {
                client.putDirectory(paths[idx], headers);
            }
        }
    }
}
