package net.onelitefeather.pandorascluster.api;

import net.onelitefeather.pandorascluster.api.service.*;
import net.onelitefeather.pandorascluster.database.service.*;
import org.hibernate.HibernateException;
import org.hibernate.cfg.Configuration;

public class PandorasClusterApiImpl implements PandorasCluster, ThreadHelper {

    private DatabaseService databaseService;
    private LandPlayerService landPlayerService;
    private LandFlagService landFlagService;
    private LandService landService;
    private StaffNotificationService staffNotificationService;

    public PandorasClusterApiImpl() {

        syncThreadForServiceLoader(() -> {
            try {
                var sessionFactory = new Configuration().configure().configure("connection.cfg.xml").buildSessionFactory();
                this.databaseService = new DatabaseServiceImpl(sessionFactory);
            } catch (HibernateException e) {
                this.databaseService = null;
                throw new HibernateException("Cannot build session factorty.", e);
            }
        });

        if (databaseService == null) return;
        this.landPlayerService = new DatabaseLandPlayerService(databaseService);
        this.landFlagService = new DatabaseLandFlagService(this);
        this.landService = new DatabaseLandService(this);
        this.staffNotificationService = new StaffNotificationService();
    }

    @Override
    public LandPlayerService getLandPlayerService() {
        return this.landPlayerService;
    }

    @Override
    public DatabaseService getDatabaseService() {
        return this.databaseService;
    }

    @Override
    public LandService getLandService() {
        return this.landService;
    }

    @Override
    public LandFlagService getLandFlagService() {
        return this.landFlagService;
    }

    @Override
    public StaffNotificationService getStaffNotification() {
        return this.staffNotificationService;
    }
}
