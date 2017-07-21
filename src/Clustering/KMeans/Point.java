package Clustering.KMeans;

public class Point {
  private int x;
  private int y;

  Point(int x, int y) {
    this.x = x;
    this.y = y;
  }

  int getX() {
    return x;
  }

  int getY() {
    return y;
  }

  void setX(int x) {
    this.x = x;
  }

  void setY(int y) {
    this.y = y;
  }

  double distance(Point p) {
    return Math.abs(this.getX() - p.getX()) + Math.abs(this.getY() - p.getY());
  }

  Point sumPoints(Point p) {
    return new Point(this.getX() + p.getX(), this.getY() + p.getY());
  }

  @Override
  public String toString() {
    return "(" + this.getX() + ", " + this.getY() + ")";
  }

  @Override
  public boolean equals(Object that) {
    if (!(that instanceof Point)) {
      return false;
    }

    Point thatPoint = (Point) that;

    return thatPoint.getX() == getX() && thatPoint.getY() == getY();
  }

  @Override
  public int hashCode() {
    int result = x;
    result = 31 * result + y;
    return result;
  }
}
