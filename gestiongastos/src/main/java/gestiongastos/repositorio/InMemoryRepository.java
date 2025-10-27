package gestiongastos.repositorio;

import java.util.*;
import java.util.function.Function;

public class InMemoryRepository<T, K> implements Repository<T, K> {

    private final Map<K, T> storage = new LinkedHashMap<>();
    private final Function<T, K> idGetter;

    public InMemoryRepository(Function<T, K> idGetter) {
    	
        this.idGetter = Objects.requireNonNull(idGetter);
    }

    @Override public T save(T entity) {
    	
        storage.put(idGetter.apply(entity), entity);
        return entity;
    }

    @Override public Optional<T> findById(K id) { 
    	
    	return Optional.ofNullable(storage.get(id)); 
    	}

    @Override public List<T> findAll() { 
    	
    	return new ArrayList<>(storage.values()); 
    }

    @Override public void deleteById(K id) { 
    	
    	storage.remove(id); 
    	}

    @Override public void deleteAll() { 
    	
    	storage.clear(); 
    
    }
}
