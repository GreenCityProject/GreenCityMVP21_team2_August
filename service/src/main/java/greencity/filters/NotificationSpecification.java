package greencity.filters;

import greencity.entity.Notification;
import greencity.entity.Notification_;
import greencity.entity.User;
import greencity.entity.User_;
import jakarta.persistence.criteria.*;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;

import java.util.List;

@RequiredArgsConstructor
public class NotificationSpecification implements MySpecification<Notification> {
    private final transient List<SearchCriteria> criteria;

    @Override
    public Predicate toPredicate(@NotNull final Root<Notification> root, @NotNull final CriteriaQuery<?> query,
                                 final CriteriaBuilder criteriaBuilder) {
        return criteria.stream().map(creation -> getPredicate(root, criteriaBuilder, creation)).
                reduce(criteriaBuilder.conjunction(), criteriaBuilder::and);
    }

    private Predicate getPredicate(final Root<Notification> root, final CriteriaBuilder criteriaBuilder, final SearchCriteria searchCriteria) {
        return switch (searchCriteria.getKey()) {
            case Notification_.USER + "_" + User_.ID -> getPredicateForUser(root, criteriaBuilder, searchCriteria);
            case Notification_.TYPE, Notification_.PROJECT_NAME ->
                    getEnumPredicate(root, criteriaBuilder, searchCriteria);
            case Notification_.VIEWED -> criteriaBuilder.equal(root.get(Notification_.VIEWED),
                    Boolean.parseBoolean(searchCriteria.getValue().toString()));
            default -> criteriaBuilder.conjunction();
        };
    }

    private Predicate getPredicateForUser(final Root<Notification> root, final CriteriaBuilder criteriaBuilder,
                                          final SearchCriteria searchCriteria) {
        final Join<Notification, User> join = root.join(Notification_.USER);
        return criteriaBuilder.equal(join.get(User_.ID), searchCriteria.getValue());
    }
}
