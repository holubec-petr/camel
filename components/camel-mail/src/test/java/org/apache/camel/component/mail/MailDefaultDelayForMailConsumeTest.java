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
package org.apache.camel.component.mail;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mail.Mailbox.MailboxUser;
import org.apache.camel.component.mail.Mailbox.Protocol;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.junit5.CamelTestSupport;
import org.apache.camel.util.StopWatch;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Unit test for testing mail polling is happening according to the default poll interval.
 */
public class MailDefaultDelayForMailConsumeTest extends CamelTestSupport {
    @SuppressWarnings({ "checkstyle:ConstantName" })
    private static final MailboxUser bond = Mailbox.getOrCreateUser("bond", "secret");

    @Test
    public void testConsuming() throws Exception {
        // clear mailbox
        Mailbox.clearAll();

        MockEndpoint mock = getMockEndpoint("mock:result");
        mock.expectedBodiesReceived("Hello London");

        template.sendBody(bond.uriPrefix(Protocol.smtp), "Hello London");
        // first poll should happen immediately
        mock.setResultWaitTime(2000L);
        mock.assertIsSatisfied();

        mock.reset();
        mock.expectedBodiesReceived("Hello Paris");
        mock.setResultWaitTime(1000L + 2000L);

        StopWatch watch = new StopWatch();

        template.sendBody(bond.uriPrefix(Protocol.smtp), "Hello Paris");

        // poll next mail and that is should be done within the default delay (overrule to 1 sec) + 2 sec slack
        mock.assertIsSatisfied();

        long delta = watch.taken();
        assertTrue(delta > 1000 - 1000L, "Camel should not default poll the mailbox to often");
    }

    @Override
    protected RouteBuilder createRouteBuilder() {
        return new RouteBuilder() {
            public void configure() {
                // we overrule the default of 60 sec to 1 so the unit test is faster
                from(bond.uriPrefix(Protocol.imap) + "&delay=1000")
                        .to("mock:result");
            }
        };
    }
}
