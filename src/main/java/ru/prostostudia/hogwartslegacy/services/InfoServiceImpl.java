package ru.prostostudia.hogwartslegacy.services;

import org.springframework.stereotype.Service;
import ru.prostostudia.hogwartslegacy.interfaces.InfoService;

import java.util.stream.Stream;

@Service
public class InfoServiceImpl implements InfoService {

    @Override
    public int getCalculateSum() {
        return Stream.iterate(1, a -> a + 1)
                .limit(1_000_000)
                .parallel()
                .reduce(0, Integer::sum);
    }
}
