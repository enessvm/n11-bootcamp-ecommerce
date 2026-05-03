import { useState } from 'react';
import { MapPin, Plus } from 'lucide-react';
import {
  useAddresses,
  useCreateAddress,
  useDeleteAddress,
  useUpdateAddress,
} from '@/hooks/useAddresses';
import { AddressCard } from '@/components/address/AddressCard';
import { AddressForm } from '@/components/address/AddressForm';
import { Button } from '@/components/ui/button';
import { Skeleton } from '@/components/ui/skeleton';
import { extractErrorMessage } from '@/api/client';

const NEW = Symbol('NEW');

export function AccountAddressesPage() {
  const { data: addresses, isLoading, error } = useAddresses();
  const create = useCreateAddress();
  const update = useUpdateAddress();
  const remove = useDeleteAddress();

  // null = list view; NEW = add form; <address> = edit form
  const [editing, setEditing] = useState(null);

  const handleAdd = async (payload) => {
    await create.mutateAsync(payload);
    setEditing(null);
  };

  const handleEdit = async (payload) => {
    await update.mutateAsync({ id: editing.id, payload });
    setEditing(null);
  };

  const handleDelete = (address) => {
    if (window.confirm(`Delete the address "${address.label || address.recipientName}"?`)) {
      remove.mutate(address.id);
    }
  };

  if (isLoading) {
    return (
      <div className="space-y-4">
        <Skeleton className="h-32" />
        <Skeleton className="h-32" />
      </div>
    );
  }

  if (error) {
    return (
      <div className="rounded-md border border-destructive/30 bg-destructive/5 p-6 text-center text-sm text-destructive">
        {extractErrorMessage(error)}
      </div>
    );
  }

  if (editing != null) {
    const isNew = editing === NEW;
    return (
      <div className="rounded-lg border bg-card p-6 shadow-sm">
        <h2 className="mb-4 text-lg font-semibold">
          {isNew ? 'Add new address' : 'Edit address'}
        </h2>
        <AddressForm
          defaultValues={isNew ? undefined : editing}
          onSubmit={isNew ? handleAdd : handleEdit}
          onCancel={() => setEditing(null)}
          busy={create.isPending || update.isPending}
          submitLabel={isNew ? 'Save address' : 'Save changes'}
        />
      </div>
    );
  }

  return (
    <div className="space-y-4">
      <div className="flex items-baseline justify-between">
        <h2 className="text-lg font-semibold">Saved addresses</h2>
        <Button onClick={() => setEditing(NEW)}>
          <Plus className="mr-2 h-4 w-4" /> Add new
        </Button>
      </div>

      {addresses && addresses.length > 0 ? (
        <div className="space-y-3">
          {addresses.map((addr) => (
            <AddressCard
              key={addr.id}
              address={addr}
              onEdit={(a) => setEditing(a)}
              onDelete={handleDelete}
              busy={remove.isPending}
            />
          ))}
        </div>
      ) : (
        <div className="rounded-lg border bg-card p-12 text-center">
          <MapPin className="mx-auto h-10 w-10 text-muted-foreground" />
          <p className="mt-3 text-sm font-medium text-foreground">
            No saved addresses yet
          </p>
          <p className="mt-1 text-sm text-muted-foreground">
            Add one here or during checkout.
          </p>
          <Button className="mt-4" onClick={() => setEditing(NEW)}>
            <Plus className="mr-2 h-4 w-4" /> Add your first address
          </Button>
        </div>
      )}
    </div>
  );
}
