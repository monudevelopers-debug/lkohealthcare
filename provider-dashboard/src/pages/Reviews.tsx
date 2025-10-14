import React, { useState } from 'react';
import { useQuery } from 'react-query';
import { 
  Star, 
  Search,
  Filter,
  MoreVertical,
  Eye,
  Reply,
  ThumbsUp,
  ThumbsDown,
  Calendar,
  User,
  MessageSquare,
  TrendingUp,
  TrendingDown
} from 'lucide-react';

import { getMyReviews } from '../services/api';
import { Review } from '../types/review';

const Reviews: React.FC = () => {
  const [searchTerm, setSearchTerm] = useState('');
  const [ratingFilter, setRatingFilter] = useState<string>('ALL');
  const [sortBy, setSortBy] = useState<'newest' | 'oldest' | 'highest' | 'lowest'>('newest');
  const [selectedReview, setSelectedReview] = useState<Review | null>(null);
  const [showReplyModal, setShowReplyModal] = useState(false);

  // Fetch provider reviews
  const { data: reviewsData, isLoading } = useQuery(
    ['provider-reviews', ratingFilter, sortBy],
    () => getMyReviews(0, 50),
    {
      refetchInterval: 60000, // Refetch every minute
    }
  );

  const reviews = reviewsData?.content || [];
  
  // Review statistics - calculated from reviews data
  const reviewStats = {
    rating1Count: reviews.filter(r => r.rating === 1).length,
    rating2Count: reviews.filter(r => r.rating === 2).length,
    rating3Count: reviews.filter(r => r.rating === 3).length,
    rating4Count: reviews.filter(r => r.rating === 4).length,
    rating5Count: reviews.filter(r => r.rating === 5).length,
  };
  const filteredReviews = reviews.filter(review =>
    review.comment.toLowerCase().includes(searchTerm.toLowerCase()) ||
    review.customer.name.toLowerCase().includes(searchTerm.toLowerCase()) ||
    review.booking.service.name.toLowerCase().includes(searchTerm.toLowerCase())
  );

  const getRatingColor = (rating: number) => {
    if (rating >= 4) return 'text-green-600';
    if (rating >= 3) return 'text-yellow-600';
    return 'text-red-600';
  };

  const getRatingBgColor = (rating: number) => {
    if (rating >= 4) return 'bg-green-100';
    if (rating >= 3) return 'bg-yellow-100';
    return 'bg-red-100';
  };

  const handleViewReview = (review: Review) => {
    setSelectedReview(review);
  };

  const handleReply = (review: Review) => {
    setSelectedReview(review);
    setShowReplyModal(true);
  };

  const renderStars = (rating: number) => {
    return (
      <div className="flex items-center">
        {[...Array(5)].map((_, i) => (
          <Star
            key={i}
            className={`w-4 h-4 ${
              i < rating ? 'text-yellow-500' : 'text-gray-300'
            }`}
            fill={i < rating ? 'currentColor' : 'none'}
          />
        ))}
      </div>
    );
  };

  const stats = [
    {
      title: 'Total Reviews',
      value: reviewStats?.totalReviews || 0,
      change: reviewStats?.reviewsChange || 0,
      trend: reviewStats?.reviewsTrend || 'up',
      icon: MessageSquare,
      color: 'blue',
    },
    {
      title: 'Average Rating',
      value: reviewStats?.averageRating || 0,
      change: reviewStats?.ratingChange || 0,
      trend: reviewStats?.ratingTrend || 'up',
      icon: Star,
      color: 'yellow',
    },
    {
      title: '5-Star Reviews',
      value: reviewStats?.fiveStarReviews || 0,
      change: reviewStats?.fiveStarChange || 0,
      trend: reviewStats?.fiveStarTrend || 'up',
      icon: TrendingUp,
      color: 'green',
    },
    {
      title: 'Response Rate',
      value: `${reviewStats?.responseRate || 0}%`,
      change: reviewStats?.responseChange || 0,
      trend: reviewStats?.responseTrend || 'up',
      icon: Reply,
      color: 'purple',
    },
  ];

  return (
    <div className="space-y-6">
      {/* Header */}
      <div className="flex justify-between items-center">
        <div>
          <h1 className="text-2xl font-bold text-gray-900">Customer Reviews</h1>
          <p className="text-gray-600">Manage and respond to customer feedback</p>
        </div>
      </div>

      {/* Review Statistics */}
      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6">
        {stats.map((stat, index) => (
          <div key={index} className="bg-white rounded-lg shadow-sm border p-6">
            <div className="flex items-center justify-between">
              <div>
                <p className="text-sm font-medium text-gray-600">{stat.title}</p>
                <p className="text-2xl font-bold text-gray-900">{stat.value}</p>
              </div>
              <div className={`p-3 rounded-full bg-${stat.color}-100`}>
                <stat.icon className={`w-6 h-6 text-${stat.color}-600`} />
              </div>
            </div>
            <div className="mt-4 flex items-center">
              {stat.trend === 'up' ? (
                <TrendingUp className="w-4 h-4 text-green-500 mr-1" />
              ) : (
                <TrendingDown className="w-4 h-4 text-red-500 mr-1" />
              )}
              <span className={`text-sm font-medium ${
                stat.trend === 'up' ? 'text-green-600' : 'text-red-600'
              }`}>
                {stat.change > 0 ? '+' : ''}{stat.change}%
              </span>
              <span className="text-sm text-gray-500 ml-2">vs previous period</span>
            </div>
          </div>
        ))}
      </div>

      {/* Rating Distribution */}
      <div className="bg-white rounded-lg shadow-sm border p-6">
        <h2 className="text-lg font-semibold text-gray-900 mb-4">Rating Distribution</h2>
        <div className="space-y-3">
          {[5, 4, 3, 2, 1].map((rating) => {
            const count = reviewStats?.ratingDistribution?.[rating] || 0;
            const percentage = reviewStats?.totalReviews ? (count / reviewStats.totalReviews) * 100 : 0;
            return (
              <div key={rating} className="flex items-center">
                <div className="flex items-center w-16">
                  <span className="text-sm font-medium text-gray-700">{rating}</span>
                  <Star className="w-4 h-4 text-yellow-500 ml-1" />
                </div>
                <div className="flex-1 mx-4">
                  <div className="w-full bg-gray-200 rounded-full h-2">
                    <div 
                      className="bg-yellow-500 h-2 rounded-full" 
                      style={{ width: `${percentage}%` }}
                    ></div>
                  </div>
                </div>
                <div className="flex items-center w-20">
                  <span className="text-sm text-gray-500">{count}</span>
                  <span className="text-sm text-gray-400 ml-1">({percentage.toFixed(1)}%)</span>
                </div>
              </div>
            );
          })}
        </div>
      </div>

      {/* Filters and Search */}
      <div className="bg-white p-6 rounded-lg shadow-sm border">
        <div className="flex flex-col sm:flex-row gap-4">
          <div className="flex-1">
            <div className="relative">
              <Search className="absolute left-3 top-1/2 transform -translate-y-1/2 text-gray-400 w-4 h-4" />
              <input
                type="text"
                placeholder="Search reviews by customer, service, or comment..."
                value={searchTerm}
                onChange={(e) => setSearchTerm(e.target.value)}
                className="w-full pl-10 pr-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent"
              />
            </div>
          </div>
          <div className="flex gap-2">
            <select
              value={ratingFilter}
              onChange={(e) => setRatingFilter(e.target.value)}
              className="px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent"
            >
              <option value="ALL">All Ratings</option>
              <option value="5">5 Stars</option>
              <option value="4">4 Stars</option>
              <option value="3">3 Stars</option>
              <option value="2">2 Stars</option>
              <option value="1">1 Star</option>
            </select>
            <select
              value={sortBy}
              onChange={(e) => setSortBy(e.target.value as any)}
              className="px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent"
            >
              <option value="newest">Newest First</option>
              <option value="oldest">Oldest First</option>
              <option value="highest">Highest Rating</option>
              <option value="lowest">Lowest Rating</option>
            </select>
            <button className="px-4 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700 flex items-center gap-2">
              <Filter className="w-4 h-4" />
              Filters
            </button>
          </div>
        </div>
      </div>

      {/* Reviews List */}
      <div className="space-y-4">
        {isLoading ? (
          <div className="p-8 text-center">
            <div className="animate-spin rounded-full h-8 w-8 border-b-2 border-blue-600 mx-auto"></div>
            <p className="mt-2 text-gray-600">Loading reviews...</p>
          </div>
        ) : filteredReviews.length === 0 ? (
          <div className="p-8 text-center">
            <MessageSquare className="w-12 h-12 text-gray-400 mx-auto mb-4" />
            <p className="text-gray-600">No reviews found</p>
          </div>
        ) : (
          filteredReviews.map((review) => (
            <div key={review.id} className="bg-white rounded-lg shadow-sm border p-6">
              <div className="flex items-start justify-between">
                <div className="flex items-start space-x-4">
                  <div className="flex-shrink-0">
                    <div className="w-10 h-10 rounded-full bg-blue-100 flex items-center justify-center">
                      <User className="w-5 h-5 text-blue-600" />
                    </div>
                  </div>
                  <div className="flex-1">
                    <div className="flex items-center space-x-3 mb-2">
                      <h3 className="text-sm font-medium text-gray-900">
                        {review.customer.name}
                      </h3>
                      <div className="flex items-center">
                        {renderStars(review.rating)}
                        <span className="ml-2 text-sm text-gray-500">
                          {review.rating}/5
                        </span>
                      </div>
                      <span className={`inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium ${getRatingBgColor(review.rating)} ${getRatingColor(review.rating)}`}>
                        {review.rating} Star{review.rating !== 1 ? 's' : ''}
                      </span>
                    </div>
                    <p className="text-sm text-gray-700 mb-3">
                      {review.comment}
                    </p>
                    <div className="flex items-center space-x-4 text-xs text-gray-500">
                      <div className="flex items-center">
                        <Calendar className="w-3 h-3 mr-1" />
                        {new Date(review.createdAt).toLocaleDateString()}
                      </div>
                      <div className="flex items-center">
                        <span>Service: {review.booking.service.name}</span>
                      </div>
                      {review.isPublic && (
                        <span className="text-green-600">Public</span>
                      )}
                    </div>
                    {review.providerReply && (
                      <div className="mt-3 p-3 bg-blue-50 rounded-lg">
                        <div className="flex items-center space-x-2 mb-2">
                          <span className="text-sm font-medium text-blue-800">Your Reply:</span>
                          <span className="text-xs text-blue-600">
                            {new Date(review.providerReply.createdAt).toLocaleDateString()}
                          </span>
                        </div>
                        <p className="text-sm text-blue-700">
                          {review.providerReply.comment}
                        </p>
                      </div>
                    )}
                  </div>
                </div>
                <div className="flex items-center space-x-2">
                  <button
                    onClick={() => handleViewReview(review)}
                    className="text-blue-600 hover:text-blue-900"
                    title="View Details"
                  >
                    <Eye className="w-4 h-4" />
                  </button>
                  {!review.providerReply && (
                    <button
                      onClick={() => handleReply(review)}
                      className="text-green-600 hover:text-green-900"
                      title="Reply to Review"
                    >
                      <Reply className="w-4 h-4" />
                    </button>
                  )}
                  <button className="text-gray-400 hover:text-gray-600">
                    <MoreVertical className="w-4 h-4" />
                  </button>
                </div>
              </div>
            </div>
          ))
        )}
      </div>

      {/* Review Details Modal */}
      {selectedReview && (
        <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50">
          <div className="bg-white rounded-lg p-6 w-full max-w-2xl">
            <h3 className="text-lg font-medium text-gray-900 mb-4">
              Review Details
            </h3>
            <div className="space-y-4">
              <div className="flex items-center space-x-4">
                <div className="w-12 h-12 rounded-full bg-blue-100 flex items-center justify-center">
                  <User className="w-6 h-6 text-blue-600" />
                </div>
                <div>
                  <h4 className="text-sm font-medium text-gray-900">{selectedReview.customer.name}</h4>
                  <div className="flex items-center">
                    {renderStars(selectedReview.rating)}
                    <span className="ml-2 text-sm text-gray-500">
                      {selectedReview.rating}/5
                    </span>
                  </div>
                </div>
              </div>
              <div>
                <label className="block text-sm font-medium text-gray-700 mb-2">Review</label>
                <p className="text-sm text-gray-900">{selectedReview.comment}</p>
              </div>
              <div className="grid grid-cols-2 gap-4">
                <div>
                  <label className="block text-sm font-medium text-gray-700 mb-2">Service</label>
                  <p className="text-sm text-gray-900">{selectedReview.booking.service.name}</p>
                </div>
                <div>
                  <label className="block text-sm font-medium text-gray-700 mb-2">Date</label>
                  <p className="text-sm text-gray-900">{new Date(selectedReview.createdAt).toLocaleDateString()}</p>
                </div>
              </div>
              {selectedReview.providerReply && (
                <div>
                  <label className="block text-sm font-medium text-gray-700 mb-2">Your Reply</label>
                  <div className="p-3 bg-blue-50 rounded-lg">
                    <p className="text-sm text-blue-700">{selectedReview.providerReply.comment}</p>
                    <p className="text-xs text-blue-600 mt-1">
                      Replied on {new Date(selectedReview.providerReply.createdAt).toLocaleDateString()}
                    </p>
                  </div>
                </div>
              )}
            </div>
            <div className="mt-6 flex justify-end">
              <button
                onClick={() => setSelectedReview(null)}
                className="px-4 py-2 text-sm font-medium text-gray-700 bg-gray-100 rounded-md hover:bg-gray-200"
              >
                Close
              </button>
            </div>
          </div>
        </div>
      )}

      {/* Reply Modal */}
      {showReplyModal && selectedReview && (
        <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50">
          <div className="bg-white rounded-lg p-6 w-full max-w-md">
            <h3 className="text-lg font-medium text-gray-900 mb-4">
              Reply to Review
            </h3>
            <div className="mb-4 p-3 bg-gray-50 rounded-lg">
              <div className="flex items-center space-x-2 mb-2">
                <span className="text-sm font-medium text-gray-700">{selectedReview.customer.name}</span>
                <div className="flex items-center">
                  {renderStars(selectedReview.rating)}
                </div>
              </div>
              <p className="text-sm text-gray-600">{selectedReview.comment}</p>
            </div>
            <form 
              onSubmit={(e) => {
                e.preventDefault();
                const formData = new FormData(e.currentTarget);
                const reply = formData.get('reply') as string;
                // Handle reply submission
                setShowReplyModal(false);
                setSelectedReview(null);
              }}
              className="space-y-4"
            >
              <div>
                <label className="block text-sm font-medium text-gray-700 mb-2">
                  Your Reply
                </label>
                <textarea
                  name="reply"
                  required
                  rows={4}
                  className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500"
                  placeholder="Write your response to this review..."
                />
              </div>
              <div className="flex justify-end space-x-3">
                <button
                  type="button"
                  onClick={() => setShowReplyModal(false)}
                  className="px-4 py-2 text-sm font-medium text-gray-700 bg-gray-100 rounded-md hover:bg-gray-200"
                >
                  Cancel
                </button>
                <button
                  type="submit"
                  className="px-4 py-2 text-sm font-medium text-white bg-blue-600 rounded-md hover:bg-blue-700"
                >
                  Send Reply
                </button>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  );
};

export default Reviews;
