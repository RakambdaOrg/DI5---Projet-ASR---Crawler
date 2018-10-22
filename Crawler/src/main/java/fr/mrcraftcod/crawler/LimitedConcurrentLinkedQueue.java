package fr.mrcraftcod.crawler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Collection;
import java.util.HashMap;
import java.util.Objects;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicReference;

public class LimitedConcurrentLinkedQueue<E> extends ConcurrentLinkedQueue<E>{
	private final Logger LOGGER = LoggerFactory.getLogger(LimitedConcurrentLinkedQueue.class);
	private final HashMap<Long, Long> lastAccess;
	private final long DELTA_MS = 1000L;
	
	public LimitedConcurrentLinkedQueue(){
		this.lastAccess = new HashMap<>();
	}
	
	public LimitedConcurrentLinkedQueue(final Collection<? extends E> c){
		super(c);
		this.lastAccess = new HashMap<>();
	}
	
	@Override
	public E poll(){
		final AtomicReference<E> obj = new AtomicReference<>(null);
		final var lastTime = lastAccess.get(Thread.currentThread().getId());
		if(Objects.nonNull(lastTime)){
			final var diff = lastTime + DELTA_MS - System.currentTimeMillis();
			if(diff > 0){
				try{
					LOGGER.info("Trying to get element too soon, sleeping thread for {} ms", diff);
					Thread.sleep(diff);
				}
				catch(final InterruptedException e){
					LOGGER.error("", e);
				}
			}
		}
		lastAccess.put(Thread.currentThread().getId(), System.currentTimeMillis());
		return super.poll();
	}
}
