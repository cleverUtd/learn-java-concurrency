package com.zclau.nonReentrantLock;

import jdk.nashorn.internal.ir.WhileNode;

import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.locks.Condition;

public class ProducerConsumer {

	private final static NonReentrantLock lock = new NonReentrantLock();
	private final static Condition notFull = lock.newCondition();
	private final static Condition notEmpty = lock.newCondition();

	private final static Queue<String> queue = new LinkedBlockingQueue<>();
	private final static int queueSize = 10;

	public static void main(String[] args) {

		Thread producer = new Thread(() -> {
			lock.lock();
			try {
				// 如果队列满了，则等待
				while (queue.size() == queueSize) {
					notEmpty.await();
				}

				// 添加元素到队列
				queue.add("ele");

				// 唤醒消费线程
				notFull.notifyAll();
			} catch (InterruptedException e) {
				e.printStackTrace();
			} finally {
				lock.unlock();
			}
		});


		Thread consumer = new Thread(() -> {
			lock.lock();
			try {
				while (0 == queue.size()) {
					notFull.await();
				}
				String ele = queue.poll();
				// 唤醒生产线程
				notEmpty.notifyAll();
			} catch (InterruptedException e) {
				e.printStackTrace();
			} finally {
				lock.unlock();
			}
		});


		producer.start();
		consumer.start();
	}
}
