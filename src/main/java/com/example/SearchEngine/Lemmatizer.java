import org.apache.lucene.morphology.LuceneMorphology;
import org.apache.lucene.morphology.russian.RussianLuceneMorphology;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Lemmatizer {
    private String text;

    public Lemmatizer(String text) {
        this.text = text;
        normalizeText();
    }

    public HashMap<String, Integer> getLemm(){
        HashMap<String, Integer> result = new HashMap<>();
        List<String> word = List.of(text.split(" "));
        word.forEach(w->{
            try{
                LuceneMorphology luceneMorph = new RussianLuceneMorphology();
                List<String> morphInfo = luceneMorph.getMorphInfo(w);

                Pattern pattern = Pattern.compile("СОЮЗ|МЕЖД|ПРЕДЛ|ЧАСТ");
                Matcher matcher = pattern.matcher(morphInfo.get(0));

                if (!matcher.find()) {
                    List<String> wordBaseForms = luceneMorph.getNormalForms(w);

                    int count = result.getOrDefault(wordBaseForms.get(0), 0);
                    result.put(wordBaseForms.get(0), count + 1);
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
        return result;
    }

    private void normalizeText(){
        text = text.toLowerCase();
        text = text.replaceAll("[,.!-?'\"\n]", "");
    }
}
