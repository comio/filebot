
package net.sourceforge.tuned;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.Map.Entry;
import java.util.prefs.Preferences;

import net.sourceforge.filebot.web.MovieDescriptor;
import net.sourceforge.tuned.PreferencesMap.SerializableAdapter;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;


public class PreferencesMapTest {
	
	private static Preferences root;
	private static Preferences strings;
	private static Preferences numbers;
	private static Preferences temp;
	private static Preferences sequence;
	
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		root = Preferences.userRoot().node("filebot-test/PreferencesMap");
		
		strings = root.node("strings");
		strings.put("1", "Firefly");
		strings.put("2", "Roswell");
		strings.put("3", "Angel");
		strings.put("4", "Dead like me");
		strings.put("5", "Babylon");
		
		numbers = root.node("numbers");
		numbers.putInt("M", 4);
		numbers.putInt("A", 5);
		numbers.putInt("X", 2);
		
		sequence = root.node("sequence");
		sequence.putInt("1", 1);
		sequence.putInt("2", 2);
		sequence.putInt("3", 3);
		
		temp = root.node("temp");
	}
	

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		root.removeNode();
	}
	

	@Test
	public void get() {
		Map<String, String> stringMap = PreferencesMap.map(strings, String.class);
		
		assertEquals("Firefly", stringMap.get("1"));
	}
	

	@Test
	public void put() {
		Map<String, String> stringMap = PreferencesMap.map(temp, String.class);
		
		stringMap.put("key", "snake");
		
		assertEquals("snake", temp.get("key", null));
	}
	

	@Test
	public void remove() throws Exception {
		Map<String, Integer> map = PreferencesMap.map(numbers, Integer.class);
		
		map.remove("A");
		
		assertFalse(Arrays.asList(numbers.keys()).contains("A"));
	}
	

	@Test
	public void clear() throws Exception {
		Map<String, Integer> map = PreferencesMap.map(temp, Integer.class);
		
		map.put("X", 42);
		
		map.clear();
		
		assertTrue(temp.keys().length == 0);
	}
	

	@Test
	public void containsKey() {
		temp.put("name", "kaya");
		
		Map<String, String> map = PreferencesMap.map(temp, String.class);
		
		assertTrue(map.containsKey("name"));
	}
	

	@Test
	public void values() {
		
		Map<String, Integer> map = PreferencesMap.map(sequence, Integer.class);
		
		Collection<Integer> list = map.values();
		
		assertTrue(list.contains(1));
		assertTrue(list.contains(2));
		assertTrue(list.contains(3));
	}
	

	@Test
	public void containsValue() {
		Map<String, String> map = PreferencesMap.map(strings, String.class);
		
		assertTrue(map.containsValue("Firefly"));
	}
	

	@Test
	public void entrySet() {
		Map<String, Integer> map = PreferencesMap.map(numbers, Integer.class);
		
		for (Entry<String, Integer> entry : map.entrySet()) {
			Integer v = entry.getValue();
			entry.setValue(v + 1);
		}
		
		assertEquals(5, numbers.getInt("M", -1));
	}
	

	@Test(expected = NumberFormatException.class)
	public void adapterException() {
		PreferencesMap.map(strings, Integer.class).values();
	}
	

	@Test
	public void containsKeyWithObjectKey() throws Exception {
		Map<String, String> map = PreferencesMap.map(strings, String.class);
		
		assertFalse(map.containsKey(new Object()));
	}
	

	@Test(expected = IllegalArgumentException.class)
	public void getWithObjectKey() throws Exception {
		Map<String, String> map = PreferencesMap.map(strings, String.class);
		
		map.get(new Object());
	}
	

	@Test
	public void serializableAdapter() {
		Map<String, MovieDescriptor> map = PreferencesMap.map(temp, new SerializableAdapter<MovieDescriptor>());
		
		MovieDescriptor movie = new MovieDescriptor("The Hitchhiker's Guide to the Galaxy", 42);
		
		map.put("movie", movie);
		
		MovieDescriptor retrieved = map.get("movie");
		
		assertEquals(movie.getImdbId(), retrieved.getImdbId());
	}
}
