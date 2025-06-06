/*******************************************************************************
 * Copyright (c) 2022 Sierra Wireless and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * and Eclipse Distribution License v1.0 which accompany this distribution.
 *
 * The Eclipse Public License is available at
 *    http://www.eclipse.org/legal/epl-v20.html
 * and the Eclipse Distribution License is available at
 *    http://www.eclipse.org/org/documents/edl-v10.html.
 *
 * Contributors:
 *     Sierra Wireless - initial API and implementation
 *******************************************************************************/
package org.eclipse.leshan.core.link.lwm2m.attributes;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.stream.Stream;

import org.eclipse.leshan.core.link.LinkParseException;
import org.eclipse.leshan.core.link.attributes.InvalidAttributeException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class DefaultLwM2mAttributeParserTest {

    private final DefaultLwM2mAttributeParser parser = new DefaultLwM2mAttributeParser();

    private static Stream<Arguments> validAttributes() {
        return Stream.of(//
                Arguments.of("pmin", LwM2mAttributes.create(LwM2mAttributes.MINIMUM_PERIOD)), //
                Arguments.of("pmin=30", LwM2mAttributes.create(LwM2mAttributes.MINIMUM_PERIOD, 30l)), //
                Arguments.of("pmax", LwM2mAttributes.create(LwM2mAttributes.MAXIMUM_PERIOD)), //
                Arguments.of("pmax=60", LwM2mAttributes.create(LwM2mAttributes.MAXIMUM_PERIOD, 60l)), //
                Arguments.of("dim=2", LwM2mAttributes.create(LwM2mAttributes.DIMENSION, 2l)), //
                Arguments.of("epmin", LwM2mAttributes.create(LwM2mAttributes.EVALUATE_MINIMUM_PERIOD)), //
                Arguments.of("epmin=30", LwM2mAttributes.create(LwM2mAttributes.EVALUATE_MINIMUM_PERIOD, 30l)), //
                Arguments.of("epmax", LwM2mAttributes.create(LwM2mAttributes.EVALUATE_MAXIMUM_PERIOD)), //
                Arguments.of("epmax=60", LwM2mAttributes.create(LwM2mAttributes.EVALUATE_MAXIMUM_PERIOD, 60l)), //
                Arguments.of("lt", LwM2mAttributes.create(LwM2mAttributes.LESSER_THAN)), //
                Arguments.of("lt=30", LwM2mAttributes.create(LwM2mAttributes.LESSER_THAN, 30d)), //
                Arguments.of("lt=-30", LwM2mAttributes.create(LwM2mAttributes.LESSER_THAN, -30d)), //
                Arguments.of("lt=30.55", LwM2mAttributes.create(LwM2mAttributes.LESSER_THAN, "30.55")), //
                Arguments.of("lt=-30.55", LwM2mAttributes.create(LwM2mAttributes.LESSER_THAN, "-30.55")), //
                Arguments.of("gt", LwM2mAttributes.create(LwM2mAttributes.GREATER_THAN)), //
                Arguments.of("gt=60", LwM2mAttributes.create(LwM2mAttributes.GREATER_THAN, 60d)), //
                Arguments.of("gt=-60", LwM2mAttributes.create(LwM2mAttributes.GREATER_THAN, -60d)), //
                Arguments.of("gt=60.55", LwM2mAttributes.create(LwM2mAttributes.GREATER_THAN, "60.55")), //
                Arguments.of("gt=-60.55", LwM2mAttributes.create(LwM2mAttributes.GREATER_THAN, "-60.55")), //
                Arguments.of("st", LwM2mAttributes.create(LwM2mAttributes.STEP)), //
                Arguments.of("st=60", LwM2mAttributes.create(LwM2mAttributes.STEP, 60d)), //
                Arguments.of("st=60.55", LwM2mAttributes.create(LwM2mAttributes.STEP, "60.55")) //
        );
    }

    @ParameterizedTest(name = "Tests {index} : {0}")
    @MethodSource("validAttributes")
    void check_one_valid_attribute(String attributeAsString, LwM2mAttribute<?> expectedValue)
            throws InvalidAttributeException {
        LwM2mAttributeSet parsed = new LwM2mAttributeSet(parser.parseUriQuery(attributeAsString));
        LwM2mAttributeSet attResult = new LwM2mAttributeSet(expectedValue);
        assertEquals(parsed, attResult);
    }

    private static String[] invalidAttributes() {
        return new String[] { //
                // MINIMUM_PERIOD
                "pmin=", //
                "pmin=1.9", //
                "pmin=-1.8", //
                "pmin=a", //
                // MAXIMUM_PERIOD
                "pmax=", //
                "pmax=-2", //
                "pmax=2.7", //
                "pmax=-2.6", //
                "pmax=bc", //
                // DIMENSION
                "dim", //
                "dim=", //
                "dim=-3", //
                "dim=3.5", //
                "dim=-3.4", //
                "dim=def", //
                // EVALUATE_MINIMUM_PERIOD
                "epmin=", //
                "epmin=-4", //
                "epmin=4.3", //
                "epmin=-4.2", //
                "epmin=ghij", //
                // EVALUATE_MAXIMUM_PERIOD
                "epmax=", //
                "epmax=-5", //
                "epmax=5.1", //
                "epmax=-5.9", //
                "epmax=klmno", //
                // LESSER_THAN
                "lt=", //
                "lt=pqrts", //
                "lt=0abc", //
                // GREATER_THAN
                "gt=", //
                "gt=uvwxyz", //
                "gt=0.xyz", //
                // STEP
                "st=", //
                "st=-6", //
                "st=-6.8", //
                // Multi-attributes
                "&pmin&pmax=60&gt=2&", //
                "&pmin&pmax=60&gt=2", //
                "pmin&pmax=60&gt=2&", //
                "pmin&pmax=60&&gt=2", //
                "pmin&pmax=60&&gt=2&&&", //
                "&&&pmin&pmax=60&&gt=2", //
        };
    }

    @ParameterizedTest(name = "Test {index} : {0}")
    @MethodSource("invalidAttributes")
    void check_invalid_attributes(String invalidAttributesAsString) {
        assertThrows(InvalidAttributeException.class,
                () -> new LwM2mAttributeSet(parser.parseUriQuery(invalidAttributesAsString)));

    }

    @Test
    void check_multiple_valid_attributes() throws LinkParseException, InvalidAttributeException {

        LwM2mAttributeSet parsed = new LwM2mAttributeSet(parser.parseUriQuery("pmin&pmax=60&gt=2"));
        LwM2mAttributeSet attResult = new LwM2mAttributeSet( //
                LwM2mAttributes.create(LwM2mAttributes.MINIMUM_PERIOD), //
                LwM2mAttributes.create(LwM2mAttributes.MAXIMUM_PERIOD, 60l), //
                LwM2mAttributes.create(LwM2mAttributes.GREATER_THAN, 2d));

        assertEquals(attResult, parsed);
    }
}
