package eu.eyan.util.tuple;

public class Tuple2<T1, T2> implements KeyValue<T1, T2> {

	private T1 left;
	private T2 right;

	public Tuple2(T1 left, T2 right) {
		this.left = left;
		this.right = right;
	}

	public T1 getLeft() {
		return left;
	}

	public T2 getRight() {
		return right;
	}

	public T1 getKey() {
		return left;
	}

	public T2 getValue() {
		return right;
	}

	public T1 getStart() {
		return left;
	}

	public T2 getEnd() {
		return right;
	}

	public static <T1, T2> Tuple2<T1, T2> of(T1 left, T2 right) {
		return new Tuple2<T1, T2>(left, right);
	}

	@Override
	public String toString() {
		return "[" + left + ", " + right + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((left == null) ? 0 : left.hashCode());
		result = prime * result + ((right == null) ? 0 : right.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		
		@SuppressWarnings("unchecked")
		Tuple2<T1, T2> other = (Tuple2<T1, T2>) obj;
		
		if (left == null) {
			if (other.left != null)
				return false;
		} else if (!left.equals(other.left))
			return false;
		if (right == null) {
			if (other.right != null)
				return false;
		} else if (!right.equals(other.right))
			return false;
		return true;
	}
}