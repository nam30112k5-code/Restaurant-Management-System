<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="t" tagdir="/WEB-INF/tags" %>

<t:layout title="Feedback">
    <div class="container py-5">
        <div class="row justify-content-center">
            <div class="col-md-6">
                <div class="card card-resto shadow-lg border-warning border-top border-3">
                    <div class="card-body p-4 text-center">
                        <h3 class="text-orange font-resto mb-3">Rate Your Experience</h3>
                        <p class="text-muted small mb-4">We value your opinion. Please rate your meal!</p>

                        <form action="Feedback" method="POST" id="feedbackForm">
                            <input type="hidden" name="appointmentId" value="${param.appointmentId}">
                            <input type="hidden" name="rating" id="ratingValue" value="5">
                            <div class="mb-4">
                                <label class="form-label text-white fw-bold d-block mb-3">How was it?</label>
                                <div class="star-rating d-flex justify-content-center gap-2">
                                    <i class="bi bi-star-fill star-icon" data-value="1" onclick="setRating(1)"></i>
                                    <i class="bi bi-star-fill star-icon" data-value="2" onclick="setRating(2)"></i>
                                    <i class="bi bi-star-fill star-icon" data-value="3" onclick="setRating(3)"></i>
                                    <i class="bi bi-star-fill star-icon" data-value="4" onclick="setRating(4)"></i>
                                    <i class="bi bi-star-fill star-icon" data-value="5" onclick="setRating(5)"></i>
                                </div>
                                <div id="ratingText" class="mt-2 text-warning fw-bold small">Excellent</div>
                            </div>
                            <div class="mb-4 text-start">
                                <label class="form-label text-muted">Your Comment</label>
                                <textarea name="content" class="form-control bg-dark text-white border-secondary" rows="4" 
                                          required placeholder="Write your review here..."></textarea>
                            </div>
                            <div class="d-grid gap-2">
                                <button type="submit" class="btn btn-resto">Submit Feedback</button>
                                <a href="History" class="btn btn-outline-light">Cancel</a>
                            </div>
                        </form>
                    </div>
                </div>
            </div>
        </div>
    </div>
</t:layout>