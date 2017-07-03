import java.util.*;
import java.util.stream.Collectors;

public class SimilarUsers {

  //returns a euclidean distance-based similarity score for person1 and person2
  static double simEuclidDistance(Set<String> prefs, Map<String, Double> p1,
                                  Map<String, Double> p2) {
    //find a list of films in common
    List<String> filmsInCommon = getFilmsInCommon(prefs, p2);

    //if no films in common, return 0
    // else return sum of euclidean distance between each film ranking
    return (prefs.parallelStream().filter(p2::containsKey)
        .collect(Collectors.toList()).isEmpty()) ? 0.0 : 1 / (1 + filmsInCommon
        .parallelStream().map(i -> Math.pow(p1.get(i) - p2.get(i), 2))
        .reduce(0.0, (x, y) -> x + y));
  }

  //returns a pearson correlation score for person1 and person2
  static double simPearsonDistance(Set<String> prefs, Map<String, Double> p1,
                                   Map<String, Double> p2) {
    //finds a list of films in common
    List<String> filmsInCommon = getFilmsInCommon(prefs, p2);

    int numFilmsInCommon = filmsInCommon.size();

    if (numFilmsInCommon == 0) {
      return 0.0;
    }

    //Add up preference scores for both people
    double sumPrefs1 = p1.keySet().stream().filter(filmsInCommon::contains)
        .map(p1::get).reduce(0.0, (x, y) -> x + y);

    double sumPrefs2 = p2.keySet().stream().filter(filmsInCommon::contains)
        .map(p2::get).reduce(0.0, (x, y) -> x + y);

    //Sum up the squares
    double sumPrefs1Sq = p1.keySet().stream().filter(filmsInCommon::contains)
        .map(p1::get).map(i -> Math.pow(i, 2)).reduce(0.0, (x, y) -> x + y);
    double sumPrefs2Sq = p2.keySet().stream().filter(filmsInCommon::contains)
        .map(p2::get).map(i -> Math.pow(i, 2)).reduce(0.0, (x, y) -> x + y);

    //Sum up the products
    double productSum = filmsInCommon.stream().map(i -> p1.get(i) * p2.get(i))
        .reduce(0.0, (x, y)
        -> x + y);

    //Calculate Pearson Score
    double numerator = productSum - (sumPrefs1 * sumPrefs2 / filmsInCommon.size());
    double denominator = Math.pow((sumPrefs1Sq - Math.pow(sumPrefs1, 2) /
        numFilmsInCommon) * (sumPrefs2Sq - Math.pow(sumPrefs2, 2) /
        numFilmsInCommon), 0.5);

    return (denominator <= 0) ? 0.0 : (numerator / denominator);
  }

  private static List<String> getFilmsInCommon(Set<String> prefs, Map<String,
      Double> p2) {
    return prefs.parallelStream().filter(p2::containsKey)
        .collect(Collectors.toList());
  }
}
