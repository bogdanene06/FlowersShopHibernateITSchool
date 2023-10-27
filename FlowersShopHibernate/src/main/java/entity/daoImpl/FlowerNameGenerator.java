/*
Author: Ene Bogdan
Country: Romania
*/
package entity.daoImpl;

import lombok.extern.java.Log;
import lombok.extern.log4j.Log4j2;

import java.util.Random;


@Log
@Log4j2
public class FlowerNameGenerator {
    private static final String[] flowerNames = {
            "Rose", "Tulip", "Lily", "Daisy", "Sunflower", "Daffodil", "Carnation", "Orchid", "Peony", "Hyacinth"
    };

    public static String generateRandomFlowerName() {
        Random random = new Random();
        int randomIndex = random.nextInt(flowerNames.length);
        return flowerNames[randomIndex];
    }

}