package com.joeyjohnjo.passwordgen.gerencia;

import org.springframework.stereotype.Component;
import reactor.core.publisher.DirectProcessor;
import reactor.core.publisher.FluxProcessor;
import reactor.core.publisher.FluxSink;

import javax.annotation.PostConstruct;
import java.util.PriorityQueue;
import java.util.Queue;

@Component
public class PasswordManager {
    private FluxProcessor processor;
    private FluxSink sink;
    public static Queue<Password> queue = new PriorityQueue<>();
    private Password lastCalled;

    @PostConstruct
    private void init() {
        processor = DirectProcessor.create().serialize();
        sink = processor.sink();
    }

    //Return the next password in the queue
    public String next() {
        Password removed = queue.poll();
        if (removed != null) {
            sink.next(new History(removed, lastCalled));
            lastCalled = removed;
            return removed.get();
        }
        else return "The queue is empty";
    }

    //Return the most recently called password one more time
    public String recall() {
        if (lastCalled == null)
            return "No Passwords called yet";
        else {
            sink.next(lastCalled.get());
            return lastCalled.get();
        }
    }

    public void reset() {
        Password.lastNumber = 0;
    }

    public FluxProcessor getProcessor() {
        return processor;
    }

    private class History {
        Password current, previous;
        History(Password current, Password previous) {
            this.current = current;
            this.previous = previous;
        }

        public String getCurrent() {
            return current.get();
        }

        public String getPrevious() {
            return previous.get();
        }
    }
}
