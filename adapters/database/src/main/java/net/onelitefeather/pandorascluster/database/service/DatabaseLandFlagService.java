package net.onelitefeather.pandorascluster.database.service;

import net.onelitefeather.pandorascluster.api.PandorasCluster;
import net.onelitefeather.pandorascluster.api.flag.Flag;
import net.onelitefeather.pandorascluster.api.flag.types.RoleFlag;
import net.onelitefeather.pandorascluster.api.land.Land;
import net.onelitefeather.pandorascluster.api.land.LandArea;
import net.onelitefeather.pandorascluster.api.service.DatabaseService;
import net.onelitefeather.pandorascluster.api.service.LandFlagService;
import net.onelitefeather.pandorascluster.database.models.flag.RoleFlagEntity;
import net.onelitefeather.pandorascluster.database.models.land.LandAreaEntity;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.jetbrains.annotations.NotNull;

public final class DatabaseLandFlagService implements LandFlagService {

    private final PandorasCluster pandorasCluster;
    private final DatabaseService databaseService;

    public DatabaseLandFlagService(PandorasCluster pandorasCluster) {
        this.pandorasCluster = pandorasCluster;
        this.databaseService = pandorasCluster.getDatabaseService();
    }

    @Override
    public void addRoleFlag(@NotNull RoleFlag roleFlag, LandArea landArea) {
        Transaction transaction = null;

        try (Session session = this.databaseService.sessionFactory().openSession()) {
            transaction = session.beginTransaction();

            LandAreaEntity landAreaEntity = (LandAreaEntity) databaseService.landAreaMapper().modelToEntity(landArea);
            var flag = new RoleFlagEntity(null, roleFlag.getName(), roleFlag.getDefaultState(), roleFlag.getRole(), landAreaEntity);

            session.persist(flag);
            transaction.commit();
        } catch (HibernateException e) {
            if (transaction != null) transaction.rollback();
        }

    }

    @Override
    public void updateLandFlag(@NotNull Flag<?> flag, @NotNull Land land) {

    }

    @Override
    public void removeLandFlag(@NotNull Flag<?> flag, @NotNull Land land) {

    }
}
