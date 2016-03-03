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

package gaffer.data.element;

import gaffer.exception.SerialisationException;
import gaffer.jsonserialisation.JSONSerialiser;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Date;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@RunWith(MockitoJUnitRunner.class)
public class EdgeTest extends ElementTest {

    @Test
    public void shouldSetAndGetFields() {
        // Given
        final Edge edge = new Edge("group");

        // When
        edge.setSource("source vertex");
        edge.setDestination("dest vertex");
        edge.setDirected(true);

        // Then
        assertEquals("group", edge.getGroup());
        assertEquals("source vertex", edge.getSource());
        assertEquals("dest vertex", edge.getDestination());
        assertTrue(edge.isDirected());
    }

    @Test
    public void shouldReturnTrueForEqualsWithTheSameInstance() {
        // Given
        final Edge edge = new Edge("group");
        edge.setSource("source vertex");
        edge.setDestination("dest vertex");
        edge.setDirected(true);

        // When
        boolean isEqual = edge.equals(edge);

        // Then
        assertTrue(isEqual);
        assertEquals(edge.hashCode(), edge.hashCode());
    }

    @Test
    public void shouldReturnTrueForEqualsWhenAllCoreFieldsAreEqual() {
        // Given
        final Edge edge1 = new Edge("group");
        edge1.setSource("source vertex");
        edge1.setDestination("dest vertex");
        edge1.setDirected(true);
        edge1.putProperty("some property", "some value");

        final Edge edge2 = cloneCoreFields(edge1);
        edge2.putProperty("some different property", "some other value");

        // When
        boolean isEqual = edge1.equals((Object) edge2);

        // Then
        assertTrue(isEqual);
        assertEquals(edge1.hashCode(), edge2.hashCode());
    }

    @Test
    public void shouldReturnFalseForEqualsWhenGroupIsDifferent() {
        // Given
        final Edge edge1 = new Edge("group");
        edge1.setSource("source vertex");
        edge1.setDestination("dest vertex");
        edge1.setDirected(true);

        final Edge edge2 = new Edge("a different group");
        edge2.setSource(edge1.getSource());
        edge2.setDestination(edge1.getDestination());
        edge2.setDirected(edge1.isDirected());

        // When
        boolean isEqual = edge1.equals((Object) edge2);

        // Then
        assertFalse(isEqual);
        assertFalse(edge1.hashCode() == edge2.hashCode());
    }

    @Test
    public void shouldReturnFalseForEqualsWhenDirectedIsDifferent() {
        // Given
        final Edge edge1 = new Edge("group");
        edge1.setSource("source vertex");
        edge1.setDestination("dest vertex");
        edge1.setDirected(true);

        final Edge edge2 = cloneCoreFields(edge1);
        edge2.setDirected(!edge1.isDirected());

        // When
        boolean isEqual = edge1.equals((Object) edge2);

        // Then
        assertFalse(isEqual);
        assertFalse(edge1.hashCode() == edge2.hashCode());
    }

    @Test
    public void shouldReturnFalseForEqualsWhenSourceIsDifferent() {
        // Given
        final Edge edge1 = new Edge("group");
        edge1.setSource("source vertex");
        edge1.setDestination("dest vertex");
        edge1.setDirected(true);

        final Edge edge2 = cloneCoreFields(edge1);
        edge2.setSource("different source");

        // When
        boolean isEqual = edge1.equals((Object) edge2);

        // Then
        assertFalse(isEqual);
        assertFalse(edge1.hashCode() == edge2.hashCode());
    }

    @Test
    public void shouldReturnFalseForEqualsWhenDestinationIsDifferent() {
        // Given
        final Edge edge1 = new Edge("group");
        edge1.setSource("source vertex");
        edge1.setDestination("dest vertex");
        edge1.setDirected(true);

        final Edge edge2 = cloneCoreFields(edge1);
        edge2.setDestination("different dest vertex");

        // When
        boolean isEqual = edge1.equals((Object) edge2);

        // Then
        assertFalse(isEqual);
        assertFalse(edge1.hashCode() == edge2.hashCode());
    }

    @Test
    public void shouldReturnTrueForEqualsWhenUndirectedIdentifiersFlipped() {
        // Given
        final Edge edge1 = new Edge("group");
        edge1.setSource("source vertex");
        edge1.setDestination("dest vertex");
        edge1.setDirected(false);

        // Given
        final Edge edge2 = new Edge("group");
        edge2.setSource("dest vertex");
        edge2.setDestination("source vertex");
        edge2.setDirected(false);

        // When
        boolean isEqual = edge1.equals((Object) edge2);

        // Then
        assertTrue(isEqual);
        assertTrue(edge1.hashCode() == edge2.hashCode());
    }

    @Test
    public void shouldReturnFalseForEqualsWhenDirectedIdentifiersFlipped() {
        // Given
        final Edge edge1 = new Edge("group");
        edge1.setSource("source vertex");
        edge1.setDestination("dest vertex");
        edge1.setDirected(true);

        // Given
        final Edge edge2 = new Edge("group");
        edge2.setSource("dest vertex");
        edge2.setDestination("source vertex");
        edge2.setDirected(true);

        // When
        boolean isEqual = edge1.equals((Object) edge2);

        // Then
        assertFalse(isEqual);
        assertFalse(edge1.hashCode() == edge2.hashCode());
    }

    @Test
    public void shouldSerialiseAndDeserialiseIdentifiers() throws SerialisationException {
        // Given
        final Edge edge = newElement("group");
        edge.setSource(1L);
        edge.setDestination(new Date(2L));
        edge.setDirected(true);

        final JSONSerialiser serialiser = new JSONSerialiser();

        // When
        final byte[] serialisedElement = serialiser.serialise(edge);
        final Edge deserialisedElement = serialiser.deserialise(serialisedElement, edge.getClass());

        // Then
        assertEquals(edge, deserialisedElement);
    }

    @Override
    protected Edge newElement(final String group) {
        return new Edge(group);
    }

    @Override
    protected Edge newElement() {
        return new Edge();
    }

    private Edge cloneCoreFields(final Edge edge) {
        final Edge newEdge = new Edge(edge.getGroup());
        newEdge.setSource(edge.getSource());
        newEdge.setDestination(edge.getDestination());
        newEdge.setDirected(edge.isDirected());

        return newEdge;
    }
}
