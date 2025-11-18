# TASK: Add Reviews UI to Numina Android App

> **IMPORTANT**: Check for `.task-reviews-ui-completed` before starting.
> **When finished**, create `.task-reviews-ui-completed` file.

## 🎯 OBJECTIVE

Build UI for reading and writing class/trainer reviews.

## 📋 REQUIREMENTS

### Features
1. **View Reviews**
   - Reviews list on class detail page
   - Star ratings display
   - Review content with pros/cons
   - Photos in reviews
   - Helpful voting
   - Sort by: recent, helpful, rating

2. **Write Review**
   - Rate class (1-5 stars)
   - Write review with title and content
   - Add pros and cons (optional)
   - Upload photos (optional)
   - Post and see immediately

3. **My Reviews**
   - List my reviews
   - Edit reviews (within 30 days)
   - Delete reviews

4. **Pending Reviews**
   - Classes eligible for review (attended but not reviewed)
   - Quick review prompts

### Screens
- `ReviewsListScreen.kt` - View all reviews for class/trainer
- `WriteReviewScreen.kt` - Write new review
- `MyReviewsScreen.kt` - User's reviews
- `PendingReviewsScreen.kt` - Classes to review

### Components
- `ReviewItem.kt` - Review card
- `StarRating.kt` - Star rating display/input
- `ReviewForm.kt` - Review input form
- `RatingSummary.kt` - Average rating display

### ViewModels
- `ReviewsViewModel.kt` - View reviews, vote helpful
- `WriteReviewViewModel.kt` - Create/edit review
- `MyReviewsViewModel.kt` - User's reviews

### API Integration
- `POST /api/v1/reviews/classes/{classId}` - Create review
- `GET /api/v1/reviews/classes/{classId}` - Get reviews
- `PUT /api/v1/reviews/{reviewId}` - Update review
- `DELETE /api/v1/reviews/{reviewId}` - Delete review
- `POST /api/v1/reviews/{reviewId}/helpful` - Mark helpful
- `GET /api/v1/reviews/my-reviews` - My reviews
- `GET /api/v1/reviews/pending` - Classes to review

### Local Caching
- Cache reviews in Room
- Store drafts locally

## ✅ ACCEPTANCE CRITERIA

- [ ] Users can read reviews on class pages
- [ ] Star rating input works
- [ ] Review submission successful
- [ ] Photos upload correctly
- [ ] Helpful voting works
- [ ] Edit/delete own reviews
- [ ] Pending reviews list accurate
- [ ] Material Design 3 styling

## 📝 DELIVERABLES

- Review screens and components
- ViewModels and repositories
- Room entities
- Photo upload handling
- Navigation
- Tests

## 🚀 COMPLETION

1. Build: `./gradlew build`
2. Test
3. Create `.task-reviews-ui-completed`
4. Commit: "Add reviews UI with photo uploads"
5. Push: `git push -u origin claude/add-reviews-ui`

---

**Est. Time**: 60-75 min | **Priority**: MEDIUM
