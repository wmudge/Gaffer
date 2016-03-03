/*
 * Copyright 2016 Crown Copyright
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package gaffer.operation.data;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

public class EdgeSeedTest {
    @Test
    public void shouldBeRelatedToEntitySeedWhenSourceEqualsVertex() {
        // Given
        final String source = "source";
        final String destination = "destination";
        final boolean directed = true;
        final EdgeSeed seed = new EdgeSeed(source, destination, directed);
        final EntitySeed relatedSeed = mock(EntitySeed.class);

        given(relatedSeed.getVertex()).willReturn(source);

        // When
        final boolean isRelated = seed.isRelated((ElementSeed) relatedSeed).isMatch();

        // Then
        assertTrue(isRelated);
    }

    @Test
    public void shouldBeRelatedToEntitySeedWhenDestinationEqualsVertex() {
        // Given
        final String source = "source";
        final String destination = "destination";
        final boolean directed = true;
        final EdgeSeed seed = new EdgeSeed(source, destination, directed);
        final EntitySeed relatedSeed = mock(EntitySeed.class);

        given(relatedSeed.getVertex()).willReturn(destination);

        // When
        final boolean isRelated = seed.isRelated((ElementSeed) relatedSeed).isMatch();

        // Then
        assertTrue(isRelated);
    }

    @Test
    public void shouldBeRelatedToEntitySeedWhenSourceAndVertexAreNull() {
        // Given
        final String source = null;
        final String destination = "destination";
        final boolean directed = true;
        final EdgeSeed seed = new EdgeSeed(source, destination, directed);
        final EntitySeed relatedSeed = mock(EntitySeed.class);

        given(relatedSeed.getVertex()).willReturn(source);

        // When
        final boolean isRelated = seed.isRelated((ElementSeed) relatedSeed).isMatch();

        // Then
        assertTrue(isRelated);
    }

    @Test
    public void shouldBeRelatedToEntitySeedWhenDestinationAndVertexAreNull() {
        // Given
        final String source = "source";
        final String destination = null;
        final boolean directed = true;
        final EdgeSeed seed = new EdgeSeed(source, destination, directed);
        final EntitySeed relatedSeed = mock(EntitySeed.class);

        given(relatedSeed.getVertex()).willReturn(source);

        // When
        final boolean isRelated = seed.isRelated((ElementSeed) relatedSeed).isMatch();

        // Then
        assertTrue(isRelated);
    }

    @Test
    public void shouldNotBeRelatedToEntitySeedWhenIdentifierNotEqualToSourceOrDestination() {
        // Given
        final String source = "source";
        final String destination = "destination";
        final boolean directed = true;
        final EdgeSeed seed = new EdgeSeed(source, destination, directed);
        final EntitySeed unrelatedSeed = mock(EntitySeed.class);

        given(unrelatedSeed.getVertex()).willReturn("other identifier");

        // When
        final boolean isRelated = seed.isRelated((ElementSeed) unrelatedSeed).isMatch();

        // Then
        assertFalse(isRelated);
    }

    @Test
    public void shouldBeRelatedToEdgeSeed() {
        // Given
        final String source = "source";
        final String destination = "destination";
        final boolean directed = true;
        final EdgeSeed seed1 = new EdgeSeed(source, destination, directed);
        final EdgeSeed seed2 = new EdgeSeed(source, destination, directed);

        // When
        final boolean isRelated = seed1.isRelated(seed2).isMatch();

        // Then
        assertTrue(isRelated);
    }

    @Test
    public void shouldBeEqualWhenSourceDestinationAndDirectedEqual() {
        // Given
        final String source = "source";
        final String destination = "destination";
        final boolean directed = true;
        final EdgeSeed seed1 = new EdgeSeed(source, destination, directed);
        final EdgeSeed seed2 = new EdgeSeed(source, destination, directed);

        // When
        final boolean isEqual = seed1.equals(seed2);

        // Then
        assertTrue(isEqual);
        assertEquals(seed1.hashCode(), seed2.hashCode());
    }

    @Test
    public void shouldBeNotEqualWhenSourceNotEqual() {
        // Given
        final String source = "source";
        final String destination = "destination";
        final boolean directed = true;
        final EdgeSeed seed1 = new EdgeSeed(source, destination, directed);
        final EdgeSeed seed2 = new EdgeSeed("different source", destination, directed);

        // When
        final boolean isEqual = seed1.equals(seed2);

        // Then
        assertFalse(isEqual);
        assertNotEquals(seed1.hashCode(), seed2.hashCode());
    }

    @Test
    public void shouldBeNotEqualWhenDestinationNotEqual() {
        // Given
        final String source = "source";
        final String destination = "destination";
        final boolean directed = true;
        final EdgeSeed seed1 = new EdgeSeed(source, destination, directed);
        final EdgeSeed seed2 = new EdgeSeed(source, "different destination", directed);

        // When
        final boolean isEqual = seed1.equals(seed2);

        // Then
        assertFalse(isEqual);
    }

    @Test
    public void shouldBeNotEqualWhenDirectedNotEqual() {
        // Given
        final String source = "source";
        final String destination = "destination";
        final boolean directed = true;
        final EdgeSeed seed1 = new EdgeSeed(source, destination, directed);
        final EdgeSeed seed2 = new EdgeSeed(source, destination, false);

        // When
        final boolean isEqual = seed1.equals(seed2);

        // Then
        assertFalse(isEqual);
    }
}