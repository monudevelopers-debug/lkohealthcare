import { Helmet } from 'react-helmet-async';

interface SEOProps {
  title?: string;
  description?: string;
  keywords?: string;
  ogImage?: string;
  ogType?: string;
}

export const SEO = ({
  title = 'Lucknow Nursing HealthCare Services',
  description = 'Professional healthcare services at your doorstep. Quality nursing care, physiotherapy, and elder care services in Lucknow. Book trusted healthcare professionals with just a few clicks.',
  keywords = 'healthcare lucknow, nursing services lucknow, home nursing lucknow, physiotherapy lucknow, elder care lucknow, healthcare services, home care',
  ogImage = '/images/og-image.jpg',
  ogType = 'website',
}: SEOProps) => {
  const siteName = import.meta.env.VITE_APP_NAME;
  const fullTitle = title === siteName ? title : `${title} | ${siteName}`;

  return (
    <Helmet>
      {/* Primary Meta Tags */}
      <title>{fullTitle}</title>
      <meta name="title" content={fullTitle} />
      <meta name="description" content={description} />
      <meta name="keywords" content={keywords} />

      {/* Open Graph / Facebook */}
      <meta property="og:type" content={ogType} />
      <meta property="og:title" content={fullTitle} />
      <meta property="og:description" content={description} />
      <meta property="og:image" content={ogImage} />
      <meta property="og:site_name" content={siteName} />
      <meta property="og:locale" content="en_IN" />

      {/* Twitter */}
      <meta name="twitter:card" content="summary_large_image" />
      <meta name="twitter:title" content={fullTitle} />
      <meta name="twitter:description" content={description} />
      <meta name="twitter:image" content={ogImage} />

      {/* Additional Meta Tags */}
      <meta name="robots" content="index, follow" />
      <meta name="language" content="English" />
      <meta name="author" content="Connatecoders" />
      <meta name="geo.region" content="IN-UP" />
      <meta name="geo.placename" content="Lucknow" />

      {/* Contact Information */}
      <meta name="contact" content={import.meta.env.VITE_COMPANY_EMAIL} />
      <meta name="telephone" content={import.meta.env.VITE_COMPANY_PHONE} />
    </Helmet>
  );
};
