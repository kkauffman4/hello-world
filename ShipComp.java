import java.util.Comparator;

public class ShipComp implements Comparator<Ship> {

	private String attribute;

	/***
	 * 
	 * @param attribute
	 */
	public ShipComp(String attribute) {
		this.attribute = attribute;
	}

	public int compare(Ship c1, Ship c2) {
		int result = -1;
		switch (attribute.charAt(0)) {
		case 'w':
			if (c1.getWeight() == c2.getWeight()) {
				result = 0;
			} else if (c1.getWeight() > c2.getWeight()) {
				result = 1;
			} else {
				result = -1;
			}
			break;
		case 'l':
			if (c1.getLength() == c2.getLength()) {
				result = 0;
			} else if (c1.getLength() > c2.getLength()) {
				result = 1;
			} else {
				result = -1;
			}
			break;
		case 'd':
			if (c1.getDockTime().time == c2.getDockTime().time) {
				result = 0;
			} else if (c1.getDockTime().time > c2.getDockTime().time) {
				result = 1;
			} else {
				result = -1;
			}
			break;
		case 'W':
			if (c1.getWidth() == c2.getWidth()) {
				result = 0;
			} else if (c1.getWidth() > c2.getWidth()) {
				result = 1;
			} else {
				result = -1;
			}
			break;

		default:
			if (c1.getWidth() == c2.getWidth()) {
				result = 0;
			} else if (c1.getWidth() > c2.getWidth()) {
				result = 1;
			} else {
				result = -1;
			}
			break;
		}
		return result;
	}
}
