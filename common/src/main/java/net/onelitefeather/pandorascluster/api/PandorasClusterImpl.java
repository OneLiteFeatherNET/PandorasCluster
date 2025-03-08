package net.onelitefeather.pandorascluster.api;

import net.onelitefeather.pandorascluster.api.service.*;
import net.onelitefeather.pandorascluster.api.util.Constants;
import net.onelitefeather.pandorascluster.database.service.*;
import org.hibernate.HibernateException;
import org.hibernate.cfg.Configuration;

import java.util.logging.Level;

public class PandorasClusterImpl implements PandorasCluster, ThreadHelper {

    private DatabaseService databaseService;
    private LandPlayerService landPlayerService;
    private LandFlagService landFlagService;
    private LandService landService;
    private LandAreaService landAreaService;
    private StaffNotificationService staffNotificationService;

    public PandorasClusterImpl() {

        syncThreadForServiceLoader(() -> {
            try {
                var sessionFactory = new Configuration().configure().configure("connection.cfg.xml").buildSessionFactory();
                this.databaseService = new DatabaseServiceImpl(sessionFactory);
            } catch (HibernateException e) {
                this.databaseService = null;
                Constants.LOGGER.log(Level.SEVERE, "Cannot build session factory.", e);
            }
        });

        if (databaseService == null) return;
        this.landPlayerService = new DatabaseLandPlayerService(databaseService);
        this.landFlagService = new DatabaseLandFlagService(this);
        this.landAreaService = new DatabaseLandAreaService(this, databaseService);
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

    @Override
    public LandAreaService getLandAreaService() {
        return this.landAreaService;
    }
}
