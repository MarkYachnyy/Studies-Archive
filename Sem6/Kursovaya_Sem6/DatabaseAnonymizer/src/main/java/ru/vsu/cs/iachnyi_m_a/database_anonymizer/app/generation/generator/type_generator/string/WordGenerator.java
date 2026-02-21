package ru.vsu.cs.iachnyi_m_a.database_anonymizer.app.generation.generator.type_generator.string;

import lombok.AllArgsConstructor;
import lombok.Data;
import ru.vsu.cs.iachnyi_m_a.database_anonymizer.app.generation.distribution.discrete.DiscreteDistribution;

import java.util.List;
import java.util.Random;

@Data
public class WordGenerator {
    private List<String> allowedCharacters;
    private DiscreteDistribution lengthDistribution;
    private Random rand = new Random();

    public WordGenerator(List<String> allowedCharacters, DiscreteDistribution lengthDistribution) {
        this.allowedCharacters = allowedCharacters;
        this.lengthDistribution = lengthDistribution;
    }

    public String generateWord() {
        StringBuilder stringBuilder = new StringBuilder();
        if(lengthDistribution != null){
            int length = lengthDistribution.next();
            for(int i = 0; i < length; i++){
                stringBuilder.append(allowedCharacters.getFirst().charAt(rand.nextInt(allowedCharacters.getFirst().length())));
            }
        } else {
            for (String allowedCharacter : allowedCharacters) {
                stringBuilder.append(allowedCharacter.charAt(rand.nextInt(allowedCharacter.length())));
            }
        }
        return stringBuilder.toString();
    }
}
