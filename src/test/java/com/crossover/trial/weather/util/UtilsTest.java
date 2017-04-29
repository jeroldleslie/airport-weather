package com.crossover.trial.weather.util;
import static org.junit.Assert.*;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class UtilsTest {

  private MockObject mockObject;
  @Before
  public void setUp() throws Exception {
    mockObject = new MockObject();
    mockObject.setName("hello");
    mockObject.setValue(10);
  }

  @After
  public void tearDown() {
    mockObject=null;
  }
  
  @Test
  public void testToJson() {
    String out = Utils.toJson(mockObject);
    assertTrue(out.contains("\"name\":\"hello\""));
    assertTrue(out.contains("\"value\":10"));
  }
  
  @Test
  public void testFromJson() {
    String json = "{\"name\":\"hello\",\"value\":10}";
    MockObject obj = Utils.fromJson(json, MockObject.class);
    assertEquals("hello", obj.getName());
    assertEquals(10, obj.getValue());
  }
  
}

class MockObject {
  String name;
  int value;

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public int getValue() {
    return value;
  }

  public void setValue(int value) {
    this.value = value;
  }

}
