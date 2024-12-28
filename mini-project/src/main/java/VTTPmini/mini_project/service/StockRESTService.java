package VTTPmini.mini_project.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import VTTPmini.mini_project.model.Stock;
import VTTPmini.mini_project.repository.StockRepository;

@Service
public class StockRESTService {
    
    @Autowired
    private StockRepository stockRepo;

    public Stock getStock(String email, String ticker){
        return stockRepo.getStock(email, ticker);
    }
    
}
