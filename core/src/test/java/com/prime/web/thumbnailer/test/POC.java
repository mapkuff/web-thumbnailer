package com.prime.web.thumbnailer.test;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.reactivex.Observable;
import io.reactivex.schedulers.Schedulers;
import net.coobird.thumbnailator.Thumbnails;

public class POC {
	
	private static Logger logger = LoggerFactory.getLogger(POC.class);
	
	public static void main(String[] args) {
		
		Observable<Integer> target = Observable.create(s -> {
			try {
				logger.info("CREATE !!");
				int i = 1;
				while (true) {
					logger.info("ON_NEXT: " + i);
					s.onNext(i++);
					Thread.sleep(1000);
				}
			} catch (Exception e) {
				s.onError(e);
			}
		});
		
		target = target
			.subscribeOn(Schedulers.io())
			.share()
			;
		
		
		target
			.observeOn(Schedulers.io())
			.map(e -> e.toString())
			.doOnNext(logger::info)
			.subscribe();
		
		target
			.observeOn(Schedulers.io())
			.map(e -> e.toString())
			.doOnNext(logger::info)
			.subscribe();
		
		
		try {
			Thread.sleep(10000000);
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}
		
	}
	
	
}
