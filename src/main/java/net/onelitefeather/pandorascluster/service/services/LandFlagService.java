package net.onelitefeather.pandorascluster.service.services;

import net.onelitefeather.pandorascluster.api.PandorasClusterApi;
import net.onelitefeather.pandorascluster.land.Land;
import net.onelitefeather.pandorascluster.land.flag.LandFlag;
import net.onelitefeather.pandorascluster.land.flag.LandFlagEntity;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Level;

public class LandFlagService {

    private final PandorasClusterApi pandorasClusterApi;

    public LandFlagService(PandorasClusterApi pandorasClusterApi) {
        this.pandorasClusterApi = pandorasClusterApi;
    }

    public LandFlagEntity getFlag(LandFlag landFlag, Land land) {

        LandFlagEntity flag = null;

        //        try (Session session = this.pandorasClusterApi.getSessionFactory().openSession()) {
//            var flagsOfLand = session.createQuery("SELECT f FROM LandFlagEntity l JOIN Land_LandFlagEntity h ON l.id = h.landFlags_id", LandFlagEntity.class);
//            flagsOfLand.setParameter("uuid", uuid.toString());
//            return landOfOwner.list();
//        } catch (HibernateException e) {
//            this.pandorasClusterApi.getLogger().log(Level.SEVERE, "Cannot load flags by land", e);
//        }

        return flag;

    }

//    public List<LandFlagEntity> getFlagsByLand(@NotNull Land land) {
//        try (Session session = this.pandorasClusterApi.getSessionFactory().openSession()) {
//            var flagsOfLand = session.createQuery("SELECT f FROM LandFlagEntity l JOIN Land_LandFlagEntity h ON l.id = h.landFlags_id", LandFlagEntity.class);
//            flagsOfLand.setParameter("uuid", uuid.toString());
//            return landOfOwner.list();
//        } catch (HibernateException e) {
//            this.pandorasClusterApi.getLogger().log(Level.SEVERE, "Cannot load flags by land", e);
//        }
//
//        return List.of();
//    }

    public void addFlags(@NotNull Land land) {
        CompletableFuture.runAsync(() -> {

            List<LandFlagEntity> flagEntities = new ArrayList<>();
            for (LandFlag landFlag : LandFlag.getFlagHashmap().values()) {
                flagEntities.add(new LandFlagEntity.Builder().
                        land(land).
                        name(landFlag.name()).
                        withType(landFlag.getFlagType()).
                        value(landFlag.getDefaultValue().toString()).
                        type(landFlag.getType()).
                        build());
            }

            Transaction transaction = null;
            try (Session session = this.pandorasClusterApi.getSessionFactory().openSession()) {
                transaction = session.beginTransaction();

                for (LandFlagEntity landFlag : flagEntities) {
                    session.persist(landFlag);
                }

                transaction.commit();
            } catch (HibernateException e) {
                if (transaction != null) {
                    transaction.rollback();
                }

                this.pandorasClusterApi.getLogger().log(Level.SEVERE, "Cannot update land", e);
            }
        });
    }
}
