package io.github.lunasaw.jdk;

import java.util.concurrent.locks.ReentrantLock;

import lombok.extern.slf4j.Slf4j;

/**
 * @author luna
 */
@Slf4j
public class ReentrantLockJdk {
    /**
     * 锁
     * ReentrantLock的加锁流程是：
     * 1，先判断是否有线程持有锁，没有加锁进行加锁
     * 2、如果加锁成功，则设置持有锁的线程是当前线程
     * 3、如果有线程持有了锁，则再去判断，是否是当前线程持有了锁
     * 4、如果是当前线程持有锁，则加锁数量（state）+1
     */
    private static ReentrantLock lock = new ReentrantLock();

    public void doSomething(int n) {
        lock.lock();
        try {
            // 进入递归第一件事：加锁
            log.info("--------lock()执行后，getState()的值：{} lock.isLocked():{}", lock.getHoldCount(), lock.isLocked());
            log.info("--------递归{}次--------", n);
            if (n <= 2) {
                this.doSomething(++n);
            } else {
                return;
            }
        } finally {
            lock.unlock();
            log.info("--------unlock()执行后，getState()的值：{} lock.isLocked():{}", lock.getHoldCount(), lock.isLocked());
        }
    }

    public static void main(String[] args) {
        ReentrantLockJdk reentrantLockJdk = new ReentrantLockJdk();
        reentrantLockJdk.doSomething(1);
        log.info("执行完doSomething方法 是否还持有锁：{}", lock.isLocked());
    }

}
