package com.raf.si.Banka2Backend.services.workerThreads;

import com.raf.si.Banka2Backend.models.mariadb.Future;
import com.raf.si.Banka2Backend.requests.FutureRequestBuySell;
import com.raf.si.Banka2Backend.services.FutureService;
import com.raf.si.Banka2Backend.services.UserService;
import lombok.SneakyThrows;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

public class FutureBuyWorker extends Thread {

    private Map<Long, FutureRequestBuySell> futuresRequestsMap = new ConcurrentHashMap<>();
    private List<FutureRequestBuySell> requestsToRemove;
    private List<Future> futuresByName;
    private FutureService futureService;
    private UserService userService;
    private boolean next = false;

    public FutureBuyWorker(FutureService futureService, UserService userService) {
        requestsToRemove = new CopyOnWriteArrayList<>();
        futuresByName = new CopyOnWriteArrayList<>();
        this.futureService = futureService;
        this.userService = userService;
    }

    @SneakyThrows
    @Override
    public void run() {
        while (true) {
            //      System.out.println("start while - " + futuresRequestsMap.size());

            //      System.out.println("prvi "  + futuresRequestsMap);

            for (Map.Entry<Long, FutureRequestBuySell> request : futuresRequestsMap.entrySet()) {
                futuresByName =
                        futureService.findFuturesByFutureName(request.getValue().getFutureName()).get();

                for (Future futureFromTable : futuresByName) {
                    if (next) continue;

                    if (request.getValue().getLimit() != 0) { // ako je postalvjen limit
                        if (futureFromTable.isForSale()
                                && futureFromTable.getMaintenanceMargin() < request.getValue().getLimit()) {
                            //              System.out.println("kupljen za limit");
                            futureFromTable.setUser(userService.findById(request.getValue().getUserId()).get());
                            futureFromTable.setForSale(false);
                            futureService.updateFuture(futureFromTable);
                            futuresRequestsMap.remove(request.getKey());
                            next = true;
                        }
                    }

                    if (request.getValue().getStop() != 0) { // ako je postalvjen stop
                        if (futureFromTable.isForSale()
                                && futureFromTable.getMaintenanceMargin() > request.getValue().getStop()) {
                            //              System.out.println("kupljen za stop");
                            futureFromTable.setUser(userService.findById(request.getValue().getUserId()).get());
                            futureFromTable.setForSale(false);
                            futureService.updateFuture(futureFromTable);
                            futuresRequestsMap.remove(request.getKey());
                            next = true;
                        }
                    }
                }
                next = false;
            }
            Thread.sleep(10000); // todo promeni ako treba duzinu sleep-a
        }
    }

    public Map<Long, FutureRequestBuySell> getFuturesRequestsMap() {
        //    System.out.println(futuresRequestsMap);
        return futuresRequestsMap;
    }

    public void setFuturesRequestsMap(Long id, FutureRequestBuySell futureRequest) {
        this.futuresRequestsMap.put(id, futureRequest);
    }

    public boolean removeFuture(Long id) {

        if (this.futuresRequestsMap.containsKey(id)) {
            this.futuresRequestsMap.remove(id);
            return false;
        }
        return true;
    }
}
