package fr.mrcraftcod.crawler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Collection;
import java.util.HashMap;
import java.util.Objects;
import java.util.concurrent.ConcurrentLinkedQueue;

public class LimitedThreadConcurrentLinkedQueue<E> extends ConcurrentLinkedQueue<E>{
	private static final long serialVersionUID = -5846252918087171521L;
	private final Logger LOGGER = LoggerFactory.getLogger(LimitedThreadConcurrentLinkedQueue.class);
	private final HashMap<Long, Long> lastAccess;
	private final long DELTA_MS = 1000L;
	
	public LimitedThreadConcurrentLinkedQueue(){
		this.lastAccess = new HashMap<>();
	}
	
	public LimitedThreadConcurrentLinkedQueue(final Collection<? extends E> c){
		super(c);
		this.lastAccess = new HashMap<>();
	}
	
	@SuppressWarnings("Duplicates")
	@Override
	public E poll(){
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
