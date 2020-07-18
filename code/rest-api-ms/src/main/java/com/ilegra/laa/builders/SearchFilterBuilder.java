package com.ilegra.laa.builders;

import com.ilegra.laa.models.SearchFilter;
import com.ilegra.laa.models.SearchGroupBy;
import com.ilegra.laa.models.SearchOrder;

public class SearchFilterBuilder {
    private SearchOrder order;
    private SearchGroupBy groupBy;
    private Integer size;

    public SearchFilterBuilder order(SearchOrder order) {
        this.order = order;
        return this;
    }

    public SearchFilterBuilder groupBy(SearchGroupBy groupBy) {
        this.groupBy = groupBy;
        return this;
    }

    public SearchFilterBuilder size(Integer size) {
        this.size = size;
        return this;
    }

    public SearchFilter createSearchFilter() {
        return new SearchFilter(order, groupBy, size);
    }
}
