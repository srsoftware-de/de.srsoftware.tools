package de.srsoftware.tools;

import org.junit.jupiter.api.Test;

import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

public class OptionalsTest {
    @Test
    public void testGetPath(){
        var map = Map.of("outer",Map.of("nested",Map.of("inner","value")));
        Optional<Object> o = Optionals.getPath("outer",map);
        assertTrue(o.isPresent());
        assertInstanceOf(Map.class, o.get());
        o = Optionals.getPath("outer.nested",map);
        assertTrue(o.isPresent());
        assertInstanceOf(Map.class, o.get());
        Optional<String> s = Optionals.getPath("outer.nested.inner",map);
        assertTrue(s.isPresent());
        assertEquals("value",s.get());
    }
}
