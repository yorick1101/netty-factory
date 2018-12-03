package me.yorick.network.message.field;

import java.util.ArrayList;
import java.util.List;

public class FieldFactory {

	private static final Field<Short> shortField = new ShortField();
	private static final Field<Integer> intField = new IntegerField();
	private static final Field<Float> floatField = new FloatField();
	private static final Field<Double> doubleField = new DoubleField();
	
	
	public static class Builder {
		
		private List<Field<?>> fields = new ArrayList<>();
		
		public Builder addShortField() {
			fields.add(shortField);
			return this;
		}
		
		public Builder addIntegerField() {
			fields.add(intField);
			return this;
		}
		
		public Builder addFloatField() {
			fields.add(floatField);
			return this;
		}
		
		public Builder addDoubleField() {
			fields.add(doubleField);
			return this;
		}
		
		public Builder addStringField(int length) {
			fields.add(new StringField(length));
			return this;
		}
		
		public Field<?>[] build(){
			return fields.toArray(new Field<?>[fields.size()]);
		}
	}
}
