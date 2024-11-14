package com.hnue.commerce.component;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.constraints.NotNull;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;

@Component
public class VisitCountFilter extends OncePerRequestFilter {
    private final AtomicInteger totalVisits = new AtomicInteger(0);

    @Override
    protected void doFilterInternal(@NotNull HttpServletRequest request,
                                    @NotNull HttpServletResponse response,
                                    @NotNull FilterChain filterChain)
            throws ServletException, IOException {
        HttpSession session = request.getSession();
        if (session.isNew() && session.getAttribute("hasVisited") == null) {
            totalVisits.incrementAndGet();
            session.setAttribute("hasVisited", true);
            //System.out.println("New session created. Total visits: " + totalVisits.get());
        }
        filterChain.doFilter(request, response);
    }

    public int getTotalVisits() {
        return totalVisits.get();
    }
}
