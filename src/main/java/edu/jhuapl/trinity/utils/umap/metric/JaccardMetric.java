/*
 * BSD 3-Clause License
 * Copyright (c) 2017, Leland McInnes, 2019 Tag.bio (Java port).
 * See UMAPLicense.txt.
 */
package edu.jhuapl.trinity.utils.umap.metric;

/*-
 * #%L
 * trinity
 * %%
 * Copyright (C) 2021 - 2023 The Johns Hopkins University Applied Physics Laboratory LLC
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

/**
 * Jaccard distance.
 *
 * @author Sean A. Irvine
 */
public final class JaccardMetric extends Metric {

    /**
     * Jaccard distance.
     */
    public static final JaccardMetric SINGLETON = new JaccardMetric();

    private JaccardMetric() {
        super(true);
    }

    @Override
    public float distance(final float[] x, final float[] y) {
        int numNonZero = 0;
        int numEqual = 0;
        for (int i = 0; i < x.length; ++i) {
            final boolean xTrue = x[i] != 0;
            final boolean yTrue = y[i] != 0;
            numNonZero += xTrue || yTrue ? 1 : 0;
            numEqual += xTrue && yTrue ? 1 : 0;
        }

        if (numNonZero == 0) {
            return 0;
        } else {
            return (numNonZero - numEqual) / (float) numNonZero;
        }
    }
}
