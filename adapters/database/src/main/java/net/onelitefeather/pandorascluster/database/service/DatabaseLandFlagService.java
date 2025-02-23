package net.onelitefeather.pandorascluster.database.service;

import net.onelitefeather.pandorascluster.api.PandorasCluster;
import net.onelitefeather.pandorascluster.api.flag.Flag;
import net.onelitefeather.pandorascluster.api.flag.types.EntityCapFlag;
import net.onelitefeather.pandorascluster.api.flag.types.NaturalFlag;
import net.onelitefeather.pandorascluster.api.flag.types.RoleFlag;
import net.onelitefeather.pandorascluster.api.land.Land;
import net.onelitefeather.pandorascluster.api.land.flag.LandEntityCapFlag;
import net.onelitefeather.pandorascluster.api.land.flag.LandNaturalFlag;
import net.onelitefeather.pandorascluster.api.land.flag.LandRoleFlag;
import net.onelitefeather.pandorascluster.api.service.DatabaseService;
import net.onelitefeather.pandorascluster.api.service.LandFlagService;
import net.onelitefeather.pandorascluster.api.util.Constants;
import net.onelitefeather.pandorascluster.database.mapper.impl.EntityCapFlagMapper;
import net.onelitefeather.pandorascluster.database.mapper.impl.LandMapper;
import net.onelitefeather.pandorascluster.database.mapper.impl.NaturalFlagMapper;
import net.onelitefeather.pandorascluster.database.mapper.impl.RoleFlagMapper;
import net.onelitefeather.pandorascluster.database.models.flag.LandNaturalFlagEntity;
import net.onelitefeather.pandorascluster.database.models.flag.LandRoleFlagEntity;
import net.onelitefeather.pandorascluster.database.models.land.LandEntity;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.jetbrains.annotations.NotNull;

import java.util.logging.Level;

public final class DatabaseLandFlagService implements LandFlagService {

    private final PandorasCluster pandorasCluster;
    private final DatabaseService databaseService;

    private LandMapper landMapper;
    private final RoleFlagMapper roleFlagMapper;
    private final NaturalFlagMapper naturalFlagMapper;
    private final EntityCapFlagMapper entityCapFlagMapper;

    public DatabaseLandFlagService(PandorasCluster pandorasCluster) {
        this.pandorasCluster = pandorasCluster;
        this.databaseService = pandorasCluster.getDatabaseService();

        this.landMapper = (LandMapper) this.databaseService.landMapper();

        this.roleFlagMapper = new RoleFlagMapper(landMapper);
        this.naturalFlagMapper = new NaturalFlagMapper(landMapper);
        this.entityCapFlagMapper = new EntityCapFlagMapper(landMapper);
    }

    @Override
    public void addRoleFlag(@NotNull RoleFlag roleFlag, Land land) {
        Transaction transaction = null;

        try (Session session = this.databaseService.sessionFactory().openSession()) {
            transaction = session.beginTransaction();

            LandEntity landEntity = (LandEntity) landMapper.modelToEntity(land);
            var flag = new LandRoleFlagEntity(null, roleFlag.getName(), roleFlag.getDefaultState(), roleFlag.getRole(), landEntity);

            session.persist(flag);
            transaction.commit();
        } catch (HibernateException e) {
            if (transaction != null) transaction.rollback();
        }
    }

    @Override
    public void addNaturalFlag(@NotNull NaturalFlag naturalFlag, Land land) {
        Transaction transaction = null;

        try (Session session = this.databaseService.sessionFactory().openSession()) {
            transaction = session.beginTransaction();

            LandEntity landEntity = (LandEntity) landMapper.modelToEntity(land);
            var flag = new LandNaturalFlagEntity(null, naturalFlag.getName(), naturalFlag.getDefaultState(), landEntity);

            session.persist(flag);
            transaction.commit();
        } catch (HibernateException e) {
            if (transaction != null) transaction.rollback();
        }
    }

    @Override
    public void addEntityCapFlag(@NotNull EntityCapFlag entityCapFlag, Land land) {

    }

    @Override
    public void updateLandFlag(@NotNull Flag<?> flag, @NotNull Land land) {

    }

    @Override
    public void removeLandRoleFlag(@NotNull LandRoleFlag roleFlag, @NotNull Land land) {
        Transaction transaction = null;
        try (Session session = this.databaseService.sessionFactory().openSession()) {
            transaction = session.beginTransaction();
            session.remove(roleFlagMapper.modelToEntity(roleFlag));
            transaction.commit();
        } catch (HibernateException e) {
            Constants.LOGGER.log(Level.SEVERE, "Cannot remove land role flag.", e);
            if (transaction != null) transaction.rollback();
        }
    }

    @Override
    public void removeLandNaturalFlag(@NotNull LandNaturalFlag naturalFlag, @NotNull Land land) {
        Transaction transaction = null;
        try (Session session = this.databaseService.sessionFactory().openSession()) {
            transaction = session.beginTransaction();
            session.remove(naturalFlagMapper.modelToEntity(naturalFlag));
            transaction.commit();
        } catch (HibernateException e) {
            Constants.LOGGER.log(Level.SEVERE, "Cannot remove land natural flag.", e);
            if (transaction != null) transaction.rollback();
        }
    }

    @Override
    public void removeLandEntityCapFlag(@NotNull LandEntityCapFlag entityCapFlag, @NotNull Land land) {
        Transaction transaction = null;
        try (Session session = this.databaseService.sessionFactory().openSession()) {
            transaction = session.beginTransaction();
            session.remove(entityCapFlagMapper.modelToEntity(entityCapFlag));
            transaction.commit();
        } catch (HibernateException e) {
            Constants.LOGGER.log(Level.SEVERE,  "Cannot remove land entity cap flag.", e);
            if (transaction != null) transaction.rollback();
        }
    }

    public EntityCapFlagMapper getEntityCapFlagMapper() {
        return entityCapFlagMapper;
    }

    public NaturalFlagMapper getNaturalFlagMapper() {
        return naturalFlagMapper;
    }

    public RoleFlagMapper getRoleFlagMapper() {
        return roleFlagMapper;
    }
}
