/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.camel.dsl.modeline;

import org.apache.camel.CamelContext;
import org.apache.camel.spi.CamelContextCustomizer;
import org.apache.camel.spi.DependencyStrategy;
import org.apache.camel.spi.Resource;

@Deprecated(since = "4.10")
public class DependencyTrait implements Trait {

    @Override
    public String getName() {
        return "dependency";
    }

    @Override
    public CamelContextCustomizer parseTrait(Resource resource, String trait) {
        return new CamelContextCustomizer() {
            @Override
            public void configure(CamelContext camelContext) {
                for (DependencyStrategy ds : camelContext.getRegistry().findByType(DependencyStrategy.class)) {
                    ds.onDependency(trait);
                }
            }
        };
    }

}
