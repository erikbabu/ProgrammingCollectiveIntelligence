import jdk.nashorn.internal.runtime.regexp.joni.exception.ValueException;

import java.util.*;
import java.util.concurrent.atomic.DoubleAdder;
import java.util.stream.Collectors;

public class Recommendations {
  public static void main(String[] args) {
    Map<String, Map<String, Double>> critics = initialiseMap();

    System.out.println("User-Based Filtering\n");

    //Get Similarity between 2 users with Euclidean Distance Score (EDS)
    System.out.println("Similarity between Lisa and Gene using " +
            "EDS: " + SimilarUsers.simEuclidDistance(critics.get("Lisa Rose")
            .keySet(),
        critics.get("Lisa Rose"), critics.get("Gene Seymour")));

    //Get Similarity between 2 users with Pearson Correlation Score (PCS)
    System.out.println("Similarity between Lisa and Gene using " +
        "PCS: " + SimilarUsers.simPearsonDistance(critics.get("Lisa Rose")
            .keySet(),
        critics.get("Lisa Rose"), critics.get("Gene Seymour")));

    //Find n most compatible reviewers in descending order with PCS
    System.out.println("\n3 most compatible reviewers to Toby:");
    topMatches(critics, "Toby", 3, ScoreType.PEARSON);

    //Get film recommendations for films a person hasn't watched, using
    // weighted calculations from PCS based on critics with similar interests
    System.out.println("\nGetting Toby some recommendations for films he has " +
        "not seen:");
    getRecommendations(critics, "Toby", ScoreType.PEARSON);

    //transform the nested map to allow different analyses on same functions
    Map<String, Map<String, Double>> transformCritics = transformMap(critics);

    //Suggest films that people who enjoy superman returns tend to enjoy
    // using PCS
    System.out.println("\nSuggesting films that people who enjoy Superman " +
        "returns might also like (PCS):");
    topMatches(transformCritics, "Superman Returns", 4, ScoreType.PEARSON);

    //Suggest films that people who enjoy snakes on a plane tend to enjoy
    // using PCS
    System.out.println("\nSuggesting films that people who enjoy snakes on a " +
        "plane might also like (PCS):");
    topMatches(transformCritics, "Snakes on a Plane", 4, ScoreType.PEARSON);

    //Suggest critics that would enjoy the film Just My Luck (PCS)
    System.out.println("\nSuggesting critics that would enjoy Just my Luck " +
        "(PCS):");
    getRecommendations(transformCritics, "Just My Luck", ScoreType.PEARSON);

    System.out.println("\n\nItem-Based filtering\n");

    System.out.println("Finding films most similar to film being compared:\n");
    Map<String, Map<String, Double>> itemComparisonDataset =
        calculateSimilarItems(transformCritics, 4);


    System.out.println("\nGives recommendations for Toby using item " +
        "similarity dataset");
    getRecommendItems(critics, itemComparisonDataset, "Toby");
  }

  static Map<String, Map<String, Double>> initialiseMap() {
    //Contains critics as well as movies they watched and scored
    Map<String, Map<String, Double>> critics = new HashMap<>();

    Map<String, Double> lisaRatings = new HashMap<>();
    lisaRatings.put("Lady in the Water", 2.5);
    lisaRatings.put("Snakes on a Plane", 3.5);
    lisaRatings.put("Just My Luck", 3.0);
    lisaRatings.put("Superman Returns", 3.5);
    lisaRatings.put("You, Me and Dupree", 2.5);
    lisaRatings.put("The Night Listener", 3.0);

    Map<String, Double> geneRatings = new HashMap<>();
    geneRatings.put("Lady in the Water", 3.0);
    geneRatings.put("Snakes on a Plane", 3.5);
    geneRatings.put("Just My Luck", 1.5);
    geneRatings.put("Superman Returns", 5.0);
    geneRatings.put("The Night Listener", 3.0);
    geneRatings.put("You, Me and Dupree", 3.5);

    Map<String, Double> michaelRatings = new HashMap<>();
    michaelRatings.put("Lady in the Water", 2.5);
    michaelRatings.put("Snakes on a Plane", 3.0);
    michaelRatings.put("Superman Returns", 3.5);
    michaelRatings.put("The Night Listener", 4.0);

    Map<String, Double> claudiaRatings = new HashMap<>();
    claudiaRatings.put("Snakes on a Plane", 3.5);
    claudiaRatings.put("Just My Luck", 3.0);
    claudiaRatings.put("The Night Listener", 4.5);
    claudiaRatings.put("Superman Returns", 4.0);
    claudiaRatings.put("You, Me and Dupree", 2.5);

    Map<String, Double> mickRatings = new HashMap<>();
    mickRatings.put("Lady in the Water", 3.0);
    mickRatings.put("Snakes on a Plane", 4.0);
    mickRatings.put("Just My Luck", 2.0);
    mickRatings.put("Superman Returns", 3.0);
    mickRatings.put("The Night Listener", 3.0);
    mickRatings.put("You, Me and Dupree", 2.0);

    Map<String, Double> jackRatings = new HashMap<>();
    jackRatings.put("Lady in the Water", 3.0);
    jackRatings.put("Snakes on a Plane", 4.0);
    jackRatings.put("The Night Listener", 3.0);
    jackRatings.put("Superman Returns", 5.0);
    jackRatings.put("You, Me and Dupree", 3.5);

    Map<String, Double> tobyRatings = new HashMap<>();
    tobyRatings.put("Snakes on a Plane", 4.5);
    tobyRatings.put("You, Me and Dupree", 1.0);
    tobyRatings.put("Superman Returns", 4.0);

    critics.put("Lisa Rose", lisaRatings);
    critics.put("Gene Seymour", geneRatings);
    critics.put("Michael Phillips", michaelRatings);
    critics.put("Claudia Puig", claudiaRatings);
    critics.put("Mick LaSalle", mickRatings);
    critics.put("Jack Matthews", jackRatings);
    critics.put("Toby", tobyRatings);

    return critics;
  }

  //////////////////////// USER-BASED FILTERING ///////////////////////////////

  static Map<String, Double> topMatches(Map<String, Map<String, Double>>
                                            critics,
                         String person, int num_results, ScoreType scoreType) {

    if (critics.size() < num_results) {
      throw new ValueException("Number too high. Not enough records to " +
          "display");
    }

    if (!critics.containsKey(person)) {
      throw new NoSuchElementException("Error: Person not located on record");
    }

    Set<String> people = critics.keySet();

    //needed for printing output
    Map<String, Double> personSimilarity = new HashMap<>();

    for (String critic : people) {
      if (critic.equals(person)) {
        continue;
      }
      //calculates similarity between every person and other critics
      double similarity = getSimilarity(critics.get(person).keySet(), critics
          .get
              (person), critics.get(critic), scoreType);

      personSimilarity.put(critic, similarity);
    }

    // pretty print the results
    printResults(personSimilarity, num_results);

    return personSimilarity;
  }

  //Get recommendations for person using weighted average of all other user's
  // rankings
  static void getRecommendations(Map<String, Map<String, Double>> critics,
                                 String person, ScoreType
                                     scoreType) {

    Set<String> people = critics.keySet();

    Map<String, Double> movieScoreMap = new HashMap<>();
    Map<String, Double> similaritySums = new HashMap<>();

    //go through each critic that isn't person
    for (String critic : people) {
      if (critic.equals(person)) {
        continue;
      }

      Set<String> personPrefs = critics.get(person).keySet();
      Set<String> criticPrefs = critics.get(critic).keySet();

      double similarity = getSimilarity(personPrefs,
          critics.get(person), critics.get(critic), scoreType);

      //ignore scores less than or equal to 0
      if (similarity <= 0) {
        continue;
      }

      for (String movie : criticPrefs) {
        //only score movies person has not seen yet
        if (!personPrefs.contains(movie)
            || critics.get(person).get(movie) == 0) {
          //weighted scores between all critics for each film added together
          movieScoreMap.putIfAbsent(movie, 0.0);
          double valueToAdd = critics.get(critic).get(movie) * similarity;
          double prevValue = movieScoreMap.get(movie);
          movieScoreMap.replace(movie, prevValue + valueToAdd);
          //sum similarities between all critics and person for each film
          similaritySums.putIfAbsent(movie, 0.0);
          double prevSim = similaritySums.get(movie);
          similaritySums.replace(movie, prevSim + similarity);
        }
      }
    }

    //Create the normalised map
    Map<String, Double> rankings = new HashMap<>();
    for (Map.Entry<String, Double> entry : movieScoreMap.entrySet()) {
      String key = entry.getKey();
      Double value = entry.getValue();
      rankings.put(key, value / similaritySums.get(key));
    }

    printResults(rankings, rankings.size());
  }


  //Generic Map that sorts items by value. Sourced from Tunaki on Stack Overflow
  private static <K, V extends Comparable<? super V>> Map<K, V> sortByValue
  (Map<K, V> map) {
    return map.entrySet()
        .stream()
        .sorted(Map.Entry.comparingByValue(Collections.reverseOrder()))
        .collect(Collectors.toMap(
            Map.Entry::getKey,
            Map.Entry::getValue,
            (e1, e2) -> e1,
            LinkedHashMap::new
        ));
  }

  //calculates similarity between a person and another critic
  private static double getSimilarity(Set<String> prefs, Map<String, Double>
      p1, Map<String,
      Double> p2, ScoreType scoreType) {
    //Enum used instead of boolean to allow for scalability of more score types
    switch (scoreType) {
      case EUCLIDEAN:
        return SimilarUsers.simEuclidDistance(prefs, p1, p2);
      case PEARSON:
        return SimilarUsers.simPearsonDistance(prefs, p1, p2);
      default:
        throw new NoSuchElementException("Error. Invalid Score Type");
    }
  }

  //pretty prints results
  private static void printResults(Map<String, Double> mapToPrint, int
      num_results) {
    //Sort by values
    Map<String, Double> sortedMapToPrint = sortByValue(mapToPrint);

    //split into two ordered lists
    List<String> listKeys = new ArrayList<>(sortedMapToPrint.keySet());
    List<Double> listValues = new ArrayList<>(sortedMapToPrint.values());

    StringBuilder sb = new StringBuilder("[");
    for (int i = 0; i < num_results; i++) {
      sb.append("( " + listValues.get(i) + "," + " '" + listKeys.get(i) +
          "')");
      if (i < num_results - 1) {
        sb.append(", ");
      }
    }
    sb.append("]");

    System.out.println(sb.toString());
  }

  //returns map with Key String replaced by Value String Component
  //sort of like a nested map flip function
  //allows us to perform different analyses with the same functions
  static Map<String, Map<String, Double>> transformMap(Map<String,
      Map<String, Double>> mapToTransform) {

    Map<String, Map<String, Double>> result = new HashMap<>();
    for (String person : mapToTransform.keySet()) {
      for (String film : mapToTransform.get(person).keySet()) {
        if (!result.containsKey(film)) {
          result.put(film, new HashMap<>());
        }
        result.get(film).put(person, mapToTransform.get(person).get(film));
      }
    }

    return result;
  }

  //////////////////////// ITEM-BASED FILTERING ///////////////////////////////

  //Shows which other items are most similar to item being compared
  //In practise, does not need to be run everytime a recommendation is needed
  //Dataset is built once and reused each time it is needed
  //Only needs to be re-run frequently enough to keep similarities up to dates
  static Map<String, Map<String, Double>> calculateSimilarItems(Map<String,
      Map<String, Double>> itemPrefs, int num_results) {

    Map<String, Map<String, Double>> result = new HashMap<>();

    for (String film : itemPrefs.keySet()) {
      System.out.println("'" + film + "': ");
      Map<String, Double> similarityToFilm = topMatches(itemPrefs, film, num_results, ScoreType.EUCLIDEAN);
      similarityToFilm = sortByValue(similarityToFilm);
      result.put(film, similarityToFilm);
      System.out.println();
    }

    return result;
  }

  //gives recommendations using item similarity dataset
  static void getRecommendItems(Map<String, Map<String, Double>> critics,
                                Map<String, Map<String, Double>> itemMatch,
                                String user) {
    Map<String, Double> userRatings = critics.get(user);
    Map<String, Double> scores = new HashMap<>();
    Map<String, Double> totalSim = new HashMap<>();

    //loop over items rated by user
    for (String film : userRatings.keySet()) {
      //loop over items similar to this item
      for (String filmCompared : itemMatch.get(film).keySet()) {
        //ignore if user already rated this item
        if (userRatings.containsKey(filmCompared)) {
          continue;
        }

        //weighted sum of rating times similarity
        scores.putIfAbsent(filmCompared, 0.0);
        double similarity = itemMatch.get(film).get(filmCompared);
        double prevValue = scores.get(filmCompared);
        double rating = userRatings.get(film);
        scores.replace(filmCompared, prevValue + (similarity * rating));

        //sum of all similarities
        totalSim.putIfAbsent(filmCompared, 0.0);
        double prevValueSum = totalSim.get(filmCompared);
        totalSim.replace(filmCompared, prevValueSum + similarity);
      }
    }

    //divide each total score by total weighting to get an average
    Map<String, Double> rankings = new HashMap<>();

    for (String film : scores.keySet()) {
      double score = scores.get(film);
      double itemTotal = totalSim.get(film);
      rankings.put(film, score / itemTotal);
    }

    //return rankings from highest to lowest
    printResults(rankings, rankings.size());
  }
}