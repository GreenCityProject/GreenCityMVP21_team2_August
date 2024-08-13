package greencity.filters;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import greencity.entity.ShoppingListItem;

import greencity.entity.ShoppingListItem_;
import greencity.entity.localization.ShoppingListItemTranslation;
import greencity.entity.localization.ShoppingListItemTranslation_;
import jakarta.persistence.criteria.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

class ShoppingListItemSpecificationTest {

    private ShoppingListItemSpecification specification;
    private List<SearchCriteria> searchCriteriaList;
    private CriteriaQuery<?> criteriaQuery;
    private CriteriaBuilder criteriaBuilder;
    private Root<ShoppingListItem> root;
    private Root<ShoppingListItemTranslation> translationRoot;

    @BeforeEach
    void setUp() {
        searchCriteriaList = new ArrayList<>();
        specification = new ShoppingListItemSpecification(searchCriteriaList);
        criteriaQuery = mock(CriteriaQuery.class);
        criteriaBuilder = mock(CriteriaBuilder.class);
        root = mock(Root.class);
        translationRoot = mock(Root.class);
    }

    @Test
    void toPredicateWithIdCriteria() {

        SearchCriteria searchCriteria = SearchCriteria.builder()
                .key("id")
                .type("id")
                .value(123L)
                .build();

        searchCriteriaList.add(searchCriteria);

        Predicate mockPredicate = mock(Predicate.class);
        when(criteriaBuilder.conjunction()).thenReturn(mockPredicate);
        when(criteriaBuilder.and(any(), any())).thenReturn(mockPredicate);

        Predicate result = specification.toPredicate(root, criteriaQuery, criteriaBuilder);

        assertEquals(mockPredicate, result);
        verify(criteriaBuilder, times(1)).and(any(), any());
    }

    @Test
    void toPredicateWithContentCriteria() {

        SearchCriteria searchCriteria = SearchCriteria.builder()
                .key("content")
                .type("content")
                .value("test content")
                .build();

        searchCriteriaList.add(searchCriteria);

        Path<String> mockContentPath = mock(Path.class);
        Path<ShoppingListItem> mockShoppingListItemRoot = mock(Path.class);
        Path<Long> mockRootIdPath = mock(Path.class);
        Path<Long> mockIdPath = mock(Path.class);
        Predicate mockPredicate = mock(Predicate.class);

        when(criteriaBuilder.conjunction()).thenReturn(mockPredicate);
        when(criteriaBuilder.and(any(), any())).thenReturn(mockPredicate);

        when(criteriaQuery.from(ShoppingListItemTranslation.class)).thenReturn(translationRoot);

        when(translationRoot.get(ShoppingListItemTranslation_.content)).thenReturn(mockContentPath);
        when(translationRoot.get(ShoppingListItemTranslation_.shoppingListItem)).thenReturn(mockShoppingListItemRoot);
        when(mockShoppingListItemRoot.get(ShoppingListItem_.id)).thenReturn(mockIdPath);
        when(root.get(ShoppingListItem_.id)).thenReturn(mockRootIdPath);

        when(criteriaBuilder.like(mockContentPath, "%test content%")).thenReturn(mockPredicate);
        when(criteriaBuilder.equal(mockIdPath, mockRootIdPath)).thenReturn(mockPredicate);

        Predicate result = specification.toPredicate(root, criteriaQuery, criteriaBuilder);

        assertEquals(mockPredicate, result);
        verify(criteriaBuilder, times(2)).and(any(), any());
        verify(criteriaBuilder, times(1)).equal(mockIdPath, mockRootIdPath);
    }
}