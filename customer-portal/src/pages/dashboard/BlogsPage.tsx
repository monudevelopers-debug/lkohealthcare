import { DocumentTextIcon, ClockIcon, UserIcon } from '@heroicons/react/24/outline';

export function BlogsPage() {
  // Placeholder blog posts - will be replaced with API data later
  const blogs = [
    {
      id: '1',
      title: 'Importance of Regular Health Check-ups at Home',
      excerpt: 'Learn why regular health monitoring at home can prevent serious medical conditions and improve quality of life for elderly family members.',
      author: 'Dr. Sharma',
      date: '2025-10-15',
      readTime: '5 min read',
      category: 'Health Tips',
      image: 'https://images.unsplash.com/photo-1576091160399-112ba8d25d1d?w=400'
    },
    {
      id: '2',
      title: 'Post-Surgery Care: What You Need to Know',
      excerpt: 'Comprehensive guide on post-operative care at home, including wound care, medication management, and when to seek medical attention.',
      author: 'Nurse Priya',
      date: '2025-10-10',
      readTime: '7 min read',
      category: 'Medical Care',
      image: 'https://images.unsplash.com/photo-1631217868264-e5b90bb7e133?w=400'
    },
    {
      id: '3',
      title: 'Benefits of Physiotherapy for Chronic Pain',
      excerpt: 'Discover how regular physiotherapy sessions can help manage chronic pain conditions and improve mobility without medication.',
      author: 'Dr. Verma',
      date: '2025-10-05',
      readTime: '6 min read',
      category: 'Physiotherapy',
      image: 'https://images.unsplash.com/photo-1576091160550-2173dba999ef?w=400'
    }
  ];

  return (
    <div className="min-h-screen bg-gray-50 py-8">
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        {/* Header */}
        <div className="text-center mb-12">
          <div className="inline-flex items-center justify-center w-16 h-16 bg-blue-100 rounded-full mb-4">
            <DocumentTextIcon className="w-8 h-8 text-blue-600" />
          </div>
          <h1 className="text-4xl font-bold text-gray-900 mb-4">Healthcare Blog</h1>
          <p className="text-xl text-gray-600 max-w-2xl mx-auto">
            Expert insights, health tips, and updates from our healthcare professionals
          </p>
        </div>

        {/* Category Filter */}
        <div className="flex flex-wrap gap-2 justify-center mb-8">
          {['All', 'Health Tips', 'Medical Care', 'Physiotherapy', 'Nutrition', 'Elderly Care'].map((category) => (
            <button
              key={category}
              className="px-4 py-2 rounded-full text-sm font-medium transition-colors bg-white border border-gray-300 text-gray-700 hover:border-blue-500 hover:text-blue-600"
            >
              {category}
            </button>
          ))}
        </div>

        {/* Blog Grid */}
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-8">
          {blogs.map((blog) => (
            <article
              key={blog.id}
              className="bg-white rounded-xl shadow-sm hover:shadow-lg transition-all duration-300 overflow-hidden group cursor-pointer"
            >
              {/* Image */}
              <div className="h-48 bg-gray-200 overflow-hidden">
                <img
                  src={blog.image}
                  alt={blog.title}
                  className="w-full h-full object-cover group-hover:scale-105 transition-transform duration-300"
                />
              </div>

              {/* Content */}
              <div className="p-6">
                {/* Category Badge */}
                <span className="inline-block px-3 py-1 bg-blue-100 text-blue-700 text-xs font-semibold rounded-full mb-3">
                  {blog.category}
                </span>

                {/* Title */}
                <h2 className="text-xl font-bold text-gray-900 mb-3 line-clamp-2 group-hover:text-blue-600 transition-colors">
                  {blog.title}
                </h2>

                {/* Excerpt */}
                <p className="text-gray-600 text-sm mb-4 line-clamp-3">
                  {blog.excerpt}
                </p>

                {/* Meta */}
                <div className="flex items-center justify-between text-xs text-gray-500 pt-4 border-t border-gray-100">
                  <div className="flex items-center gap-2">
                    <UserIcon className="w-4 h-4" />
                    <span>{blog.author}</span>
                  </div>
                  <div className="flex items-center gap-2">
                    <ClockIcon className="w-4 h-4" />
                    <span>{blog.readTime}</span>
                  </div>
                </div>
              </div>
            </article>
          ))}
        </div>

        {/* Coming Soon Notice */}
        <div className="mt-12 bg-gradient-to-r from-blue-50 to-indigo-50 border border-blue-200 rounded-xl p-8 text-center">
          <h3 className="text-xl font-semibold text-gray-900 mb-2">More Articles Coming Soon!</h3>
          <p className="text-gray-600">
            We're working on bringing you more valuable healthcare insights and tips. 
            Stay tuned for updates!
          </p>
        </div>
      </div>
    </div>
  );
}

