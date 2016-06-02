


public class Serialization {

private void serializeObject(Object obj) throws FileNotFoundException,
			IOException {
		final ObjectOutput output = new ObjectOutputStream(
				new BufferedOutputStream(new FileOutputStream(filename)));

		output.writeObject(obj);
		output.close();

	}

	private Object deserializeObject() throws FileNotFoundException,
			IOException, ClassNotFoundException {
		final ObjectInput input = new ObjectInputStream(
				new BufferedInputStream(new FileInputStream(filename)));
		final Object obj = input.readObject();
		input.close();

		return obj;
	}
}
