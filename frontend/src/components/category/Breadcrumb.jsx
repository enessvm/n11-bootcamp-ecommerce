import { Link } from 'react-router-dom';
import { ChevronRight, Home } from 'lucide-react';

export function Breadcrumb({ segments = [] }) {
  return (
    <nav aria-label="Breadcrumb" className="flex items-center gap-1 text-sm text-muted-foreground">
      <Link
        to="/"
        className="inline-flex items-center gap-1 hover:text-foreground"
        aria-label="Home"
      >
        <Home className="h-3.5 w-3.5" />
      </Link>
      {segments.map((segment, idx) => {
        const isLast = idx === segments.length - 1;
        return (
          <span key={`${segment.label}-${idx}`} className="flex items-center gap-1">
            <ChevronRight className="h-3.5 w-3.5" />
            {segment.to && !isLast ? (
              <Link to={segment.to} className="hover:text-foreground">
                {segment.label}
              </Link>
            ) : (
              <span className={isLast ? 'font-medium text-foreground' : undefined}>
                {segment.label}
              </span>
            )}
          </span>
        );
      })}
    </nav>
  );
}
