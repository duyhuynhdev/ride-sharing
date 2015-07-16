package com.bikiegang.ridesharing.dao;

import com.bikiegang.ridesharing.annn.framework.gearman.GClientManager;
import com.bikiegang.ridesharing.annn.framework.gearman.JobEnt;
import com.bikiegang.ridesharing.annn.framework.util.JSONUtil;
import com.bikiegang.ridesharing.cache.RideSharingCA;
import com.bikiegang.ridesharing.config.ConfigInfo;
import com.bikiegang.ridesharing.database.Database;
import com.bikiegang.ridesharing.pojo.Route;
import com.bikiegang.ridesharing.utilities.Const;
import java.util.HashSet;
import org.apache.log4j.Logger;

/**
 * Created by hpduy17 on 6/26/15.
 */
public class RouteDao {

    Logger logger = Logger.getLogger(this.getClass());
    RideSharingCA cache = RideSharingCA.getInstance(ConfigInfo.REDIS_SERVER);
    private Database database = Database.getInstance();

    public boolean insert(Route obj) {
        boolean result = false;
        try {
            if (obj == null) {
                return false;
            }
            //Step 1: put in hashmap
            database.getRouteHashMap().put(obj.getId(), obj);
            HashSet<Long> setRfRole = database.getRoleRFRoutes().get(obj.getRole());
            HashSet<Long> setRfUser = database.getUserIdRFRoutes().get(obj.getCreatorId());
            if (setRfRole == null) {
                setRfRole = new HashSet<>();
            }
            if (setRfUser == null) {
                setRfUser = new HashSet<>();
            }

            setRfRole.add(obj.getId());
            setRfUser.add(obj.getId());

            //Step 2: put redis
            result = cache.hset(obj.getClass().getName(), String.valueOf(obj.getId()),
                    JSONUtil.Serialize(obj));
            result &= cache.hset(obj.getClass().getName() + ":user", String.valueOf(obj.getCreatorId()),
                    JSONUtil.Serialize(setRfUser));
            result &= cache.hset(obj.getClass().getName() + ":role", String.valueOf(obj.getRole()),
                    JSONUtil.Serialize(setRfRole));

            if (result) {
                //Step 3: put job gearman
                short actionType = Const.RideSharing.ActionType.INSERT;
                JobEnt job = new JobEnt(0l, 0l, obj.getClass().getName(), actionType,
                        System.currentTimeMillis(), "", JSONUtil.Serialize(obj), "", "");
                result &= GClientManager.getInstance(ConfigInfo.RIDESHARING_WORKER_GEARMAN).push(job);
                if (!result) {
                    logger.error(String.format("Can't not insert DB with value=%s", obj));
                } else {
                    logger.info(String.format("Insert Route success with value "
                            + "=%s", JSONUtil.Serialize(obj)));
                }
            } else {
                logger.error(String.format("Can't not insert redis with key=%s, "
                        + "field=%s, value=%s",
                        obj.getClass().getName(), String.valueOf(obj.getId()),
                        JSONUtil.Serialize(obj)));
            }
        } catch (Exception ex) {
            logger.error(ex.getStackTrace());
            ex.printStackTrace();
        }
        return result;
    }

    public boolean delete(Route obj) {
        boolean result = false;
        try {
            if (obj == null) {
                return false;
            }
            //Step 1: put in hashmap
            database.getRouteHashMap().remove(obj.getId());
            HashSet<Long> mapRfRole = database.getRoleRFRoutes().get(obj.getRole());
            HashSet<Long> mapRfUser = database.getUserIdRFRoutes().get(obj.getCreatorId());
            if (mapRfRole == null) {
                mapRfRole = new HashSet<>();
            }
            if (mapRfUser == null) {
                mapRfUser = new HashSet<>();
            }

            mapRfRole.remove(obj.getId());
            mapRfUser.remove(obj.getId());

            //Step 2: put redis
            result = cache.hdel(obj.getClass().getName(),
                    String.valueOf(obj.getId()));
            result &= cache.hset(obj.getClass().getName() + ":user",
                    String.valueOf(obj.getCreatorId()), JSONUtil.Serialize(mapRfUser));
            result &= cache.hset(obj.getClass().getName() + ":role",
                    String.valueOf(obj.getCreatorId()), JSONUtil.Serialize(mapRfRole));
            if (result) {
                //Step 3: put job gearman
                short actionType = Const.RideSharing.ActionType.DELETE;
                JobEnt job = new JobEnt(0l, 0l, obj.getClass().getName(), actionType,
                        System.currentTimeMillis(), "", JSONUtil.Serialize(obj), "", "");
                result &= GClientManager.getInstance(ConfigInfo.RIDESHARING_WORKER_GEARMAN).push(job);
                if (!result) {
                    logger.error(String.format("Can't not delete DB with value=%s", obj));
                } else {
                    logger.info(String.format("Remove Route success with value "
                            + "=%s", JSONUtil.Serialize(obj)));
                }
            } else {
                logger.error(String.format("Can't not delete redis with key=%s, field=%s, value=%s",
                        obj.getClass().getName(), String.valueOf(obj.getId()), JSONUtil.Serialize(obj)));
            }
        } catch (Exception ex) {

            logger.error(ex.getStackTrace());
            ex.printStackTrace();
        }
        return result;
    }

    public boolean update(Route obj) {
        boolean result = false;
        try {
            if (obj == null) {
                return false;
            }
            //Step 1: put in hashmap
            database.getRouteHashMap().put(obj.getId(), obj);
            //Step 2: put redis
            result = cache.hset(obj.getClass().getName(), String.valueOf(obj.getId()), JSONUtil.Serialize(obj));
            if (result) {
                //Step 3: put job gearman
                short actionType = Const.RideSharing.ActionType.UPDATE;
                JobEnt job = new JobEnt(0l, 0l, obj.getClass().getName(), actionType,
                        System.currentTimeMillis(), "", JSONUtil.Serialize(obj), "", "");
                result &= GClientManager.getInstance(ConfigInfo.RIDESHARING_WORKER_GEARMAN).push(job);
                if (!result) {
                    logger.error(String.format("Can't not update DB with value=%s", obj));
                } else {
                    logger.info(String.format("Update Route success with value "
                            + "=%s", JSONUtil.Serialize(obj)));
                }
            } else {
                logger.error(String.format("Can't not update redis with key=%s, field=%s, value=%s",
                        obj.getClass().getName(), String.valueOf(obj.getId()), JSONUtil.Serialize(obj)));
            }
        } catch (Exception ex) {

            logger.error(ex.getStackTrace());
            ex.printStackTrace();
        }
        return result;
    }

}
