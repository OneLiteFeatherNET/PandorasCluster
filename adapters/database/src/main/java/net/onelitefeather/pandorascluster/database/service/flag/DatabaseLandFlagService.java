package net.onelitefeather.pandorascluster.database.service.flag;

import net.onelitefeather.pandorascluster.api.PandorasCluster;
import net.onelitefeather.pandorascluster.api.flag.FlagContainer;
import net.onelitefeather.pandorascluster.api.flag.types.EntityCapFlag;
import net.onelitefeather.pandorascluster.api.flag.types.NaturalFlag;
import net.onelitefeather.pandorascluster.api.flag.types.RoleFlag;
import net.onelitefeather.pandorascluster.api.land.flag.LandEntityCapFlag;
import net.onelitefeather.pandorascluster.api.land.flag.LandNaturalFlag;
import net.onelitefeather.pandorascluster.api.land.flag.LandRoleFlag;
import net.onelitefeather.pandorascluster.api.mapper.MapperStrategy;
import net.onelitefeather.pandorascluster.api.mapper.MappingContext;
import net.onelitefeather.pandorascluster.api.service.DatabaseService;
import net.onelitefeather.pandorascluster.api.service.LandFlagService;
import net.onelitefeather.pandorascluster.api.util.Constants;
import net.onelitefeather.pandorascluster.database.mapper.flag.FlagContainerMappingStrategy;
import net.onelitefeather.pandorascluster.database.models.flag.FlagContainerEntity;
import net.onelitefeather.pandorascluster.database.models.flag.LandEntityCapFlagEntity;
import net.onelitefeather.pandorascluster.database.models.flag.LandNaturalFlagEntity;
import net.onelitefeather.pandorascluster.database.models.flag.LandRoleFlagEntity;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.jetbrains.annotations.NotNull;

import java.util.logging.Level;

public final class DatabaseLandFlagService implements LandFlagService {

    private final PandorasCluster pandorasCluster;
    private final DatabaseService databaseService;

    public DatabaseLandFlagService(PandorasCluster pandorasCluster) {
        this.pandorasCluster = pandorasCluster;
        this.databaseService = pandorasCluster.getDatabaseService();
    }

    @Override
    public void addRoleFlag(@NotNull RoleFlag roleFlag, FlagContainer flagContainer) {
        Transaction transaction = null;

        try (SessionFactory factory = this.databaseService.sessionFactory();
             Session session = factory.openSession()) {

            transaction = session.beginTransaction();

            var flag = new LandRoleFlagEntity(null, roleFlag.getName(), roleFlag.getDefaultState(), roleFlag.getRole(), getFlagContainer(flagContainer));

            session.persist(flag);
            transaction.commit();
        } catch (HibernateException e) {
            Constants.LOGGER.log(Level.SEVERE, "Cannot add land role flag.", e);
            if (transaction != null) transaction.rollback();
        }
    }

    @Override
    public void updateRoleFlag(@NotNull LandRoleFlag roleFlag) {
        Transaction transaction = null;
        try (SessionFactory factory = this.databaseService.sessionFactory();
             Session session = factory.openSession()) {

            transaction = session.beginTransaction();
            session.merge(getRoleFlagEntity(roleFlag));
            transaction.commit();

        } catch (HibernateException e) {
            Constants.LOGGER.log(Level.SEVERE, "Cannot remove roleFlag.", e);
            if (transaction != null) transaction.rollback();
        }
    }

    @Override
    public void removeRoleFlag(@NotNull LandRoleFlag roleFlag, @NotNull FlagContainer flagContainer) {
        Transaction transaction = null;
        try (SessionFactory factory = this.databaseService.sessionFactory();
             Session session = factory.openSession()) {

            transaction = session.beginTransaction();

            flagContainer.removeRoleFlag(roleFlag);
            session.remove(getRoleFlagEntity(roleFlag));
            transaction.commit();

        } catch (HibernateException e) {
            Constants.LOGGER.log(Level.SEVERE, "Cannot remove roleFlag.", e);
            if (transaction != null) transaction.rollback();
        }
    }

    @Override
    public void addNaturalFlag(@NotNull NaturalFlag naturalFlag, FlagContainer flagContainer) {
        Transaction transaction = null;

        try (SessionFactory factory = this.databaseService.sessionFactory();
             Session session = factory.openSession()) {

            transaction = session.beginTransaction();

            var flag = new LandNaturalFlagEntity(null, naturalFlag.getName(), naturalFlag.getDefaultState(), getFlagContainer(flagContainer));
            session.persist(flag);
            transaction.commit();

        } catch (HibernateException e) {
            Constants.LOGGER.log(Level.SEVERE, "Cannot add naturalFlag.", e);
            if (transaction != null) transaction.rollback();
        }
    }

    @Override
    public void updateNaturalCapFlag(@NotNull LandNaturalFlag naturalFlag) {
        Transaction transaction = null;
        try (SessionFactory factory = this.databaseService.sessionFactory();
             Session session = factory.openSession()) {

            transaction = session.beginTransaction();
            session.merge(getNaturalFlagEntity(naturalFlag));
            transaction.commit();

        } catch (HibernateException e) {
            Constants.LOGGER.log(Level.SEVERE, "Cannot remove roleFlag.", e);
            if (transaction != null) transaction.rollback();
        }
    }

    @Override
    public void removeNaturalFlag(@NotNull LandNaturalFlag naturalFlag, @NotNull FlagContainer flagContainer) {
        Transaction transaction = null;
        try (SessionFactory factory = this.databaseService.sessionFactory();
             Session session = factory.openSession()) {
            transaction = session.beginTransaction();

            flagContainer.removeNaturalFlag(naturalFlag);
            session.remove(getNaturalFlagEntity(naturalFlag));
            transaction.commit();

        } catch (HibernateException e) {
            Constants.LOGGER.log(Level.SEVERE, "Cannot remove naturalFlag.", e);
            if (transaction != null) transaction.rollback();
        }
    }

    @Override
    public void addEntityCapFlag(@NotNull EntityCapFlag entityCapFlag, FlagContainer flagContainer) {
        Transaction transaction = null;

        try (SessionFactory factory = this.databaseService.sessionFactory();
             Session session = factory.openSession()) {

            transaction = session.beginTransaction();

            var flag = new LandEntityCapFlagEntity(null, entityCapFlag.getName(), entityCapFlag.getSpawnLimit(), getFlagContainer(flagContainer));
            session.persist(flag);
            transaction.commit();

        } catch (HibernateException e) {
            Constants.LOGGER.log(Level.SEVERE, "Cannot remove entityCap flag.", e);
            if (transaction != null) transaction.rollback();
        }
    }

    @Override
    public void updateEntityCapFlag(@NotNull LandEntityCapFlag entityCapFlag) {
        Transaction transaction = null;
        try (SessionFactory factory = this.databaseService.sessionFactory();
             Session session = factory.openSession()) {

            transaction = session.beginTransaction();
            session.merge(getEntityCapFlagEntity(entityCapFlag));
            transaction.commit();

        } catch (HibernateException e) {
            Constants.LOGGER.log(Level.SEVERE, "Cannot remove entityCap flag.", e);
            if (transaction != null) transaction.rollback();
        }
    }

    @Override
    public void removeEntityCapFlag(@NotNull LandEntityCapFlag entityCapFlag, @NotNull FlagContainer flagContainer) {
        Transaction transaction = null;
        try (SessionFactory factory = this.databaseService.sessionFactory();
             Session session = factory.openSession()) {
            transaction = session.beginTransaction();

            flagContainer.removeEntityCapFlag(entityCapFlag);
            session.remove(getEntityCapFlagEntity(entityCapFlag));
            transaction.commit();

        } catch (HibernateException e) {
            Constants.LOGGER.log(Level.SEVERE, "Cannot remove entityCap flag.", e);
            if (transaction != null) transaction.rollback();
        }
    }

    private LandEntityCapFlagEntity getEntityCapFlagEntity(LandEntityCapFlag entityCapFlag) {
        var flagContainer = getFlagContainer(entityCapFlag.getParent());
        return (LandEntityCapFlagEntity) flagContainer.entityCapFlags()
                .stream()
                .filter(entityCapFlagDBO -> entityCapFlagDBO.name().equals(entityCapFlag.getName()))
                .findFirst().orElse(null);
    }

    private LandNaturalFlagEntity getNaturalFlagEntity(LandNaturalFlag naturalFlag) {
        var flagContainer = getFlagContainer(naturalFlag.getParent());
        return (LandNaturalFlagEntity) flagContainer.entityCapFlags()
                .stream()
                .filter(naturalFlagDBO -> naturalFlagDBO.name().equals(naturalFlag.getName()))
                .findFirst().orElse(null);
    }

    private LandRoleFlagEntity getRoleFlagEntity(LandRoleFlag roleFlag) {
        var flagContainer = getFlagContainer(roleFlag.getParent());
        return (LandRoleFlagEntity) flagContainer.roleFlags()
                .stream()
                .filter(roleFlagDBO -> roleFlagDBO.name().equals(roleFlag.getName()))
                .findFirst().orElse(null);
    }

    private FlagContainerEntity getFlagContainer(FlagContainer flagContainer) {
        MappingContext mappingContext = MappingContext.create();
        mappingContext.setMappingStrategy(FlagContainerMappingStrategy.create());
        mappingContext.setMappingType(MapperStrategy.MapperType.MODEL_TO_ENTITY);
        return (FlagContainerEntity) mappingContext.doMapping(flagContainer);
    }
}
